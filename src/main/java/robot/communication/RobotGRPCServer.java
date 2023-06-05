package robot.communication;

import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.stub.StreamObserver;
import robot.RobotServiceGrpc;
import robot.RobotServiceOuterClass;
import robot.adapter.RobotInfoConverter;
import robot.context.RobotContext;
import robot.context.RobotContextProvider;
import robot.fault.handler.AccessRequestWrapper;
import robot.fault.handler.WaitingRobotsQueue;
import robot.network.RobotNetwork;
import robot.network.RobotNetworkProvider;
import robot.state.RobotState;
import robot.state.RobotStateProvider;
import robot.state.StateType;
import common.utils.Logger;

public class RobotGRPCServer {
    private final int port;
    private final Server server;

    public RobotGRPCServer(int port) {
        this.port = port;
        this.server = ServerBuilder.forPort(port)
                .addService(ServerInterceptors.intercept(new RobotServiceImpl(), new LamportServerMiddleware()))
                .build();
    }

    public void start() throws Exception {
        server.start();
        Logger.info("Robot gRPC server started, listening on " + port);
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
            Logger.info("Robot gRPC server stopped");
        }
    }

    public static class RobotServiceImpl extends RobotServiceGrpc.RobotServiceImplBase {

        private final RobotNetwork network;
        private final RobotState state;
        private final WaitingRobotsQueue queue;
        private final RobotContext context;

        public RobotServiceImpl() {
            this.network = RobotNetworkProvider.getNetwork();
            this.state = RobotStateProvider.getState();
            this.queue = WaitingRobotsQueue.getQueue();
            this.context = RobotContextProvider.getContext();
        }

        @Override
        public void sendRobotInfo(RobotServiceOuterClass.RobotInfo request, StreamObserver<Empty> responseObserver) {
            Logger.info("New robot joined the network: " + request.getId());

            network.addRobot(RobotInfoConverter.convert(request));

            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }

        @Override
        public void removeRobot(RobotServiceOuterClass.RemoveRobotRequest request, StreamObserver<Empty> responseObserver) {
            Logger.info("Robot left the network: " + request.getId());

            network.removeRobotById(request.getId());

            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }

        @Override
        public void askAccess(robot.RobotServiceOuterClass.AccessRequest request, StreamObserver<Empty> responseObserver) {
            Logger.info("Robot " + request.getId() + " asked to access the mechanic");

            synchronized (state) {

                if (state.getState() != StateType.FIXING && state.getState() != StateType.BROKEN) {
                    Logger.info("Access granted");
                    responseObserver.onNext(Empty.newBuilder().build());
                    responseObserver.onCompleted();
                } else if (state.getState() == StateType.FIXING) {
                    Logger.info("Access denied, robot is already being fixed");
                    AccessRequestWrapper wrapper = new AccessRequestWrapper(responseObserver, request.getId());
                    queue.addRobot(wrapper);
                } else {
                    if (request.getTimestamp() < state.getLastFaultTime()) {
                        Logger.info("Access granted");
                        responseObserver.onNext(Empty.newBuilder().build());
                        responseObserver.onCompleted();
                    } else if (request.getTimestamp() > state.getLastFaultTime()) {
                        Logger.info("Access denied, robot is already being fixed");
                        AccessRequestWrapper wrapper = new AccessRequestWrapper(responseObserver, request.getId());
                        queue.addRobot(wrapper);
                    } else {
                        if (request.getId().hashCode() < context.getId().hashCode()) {
                            Logger.info("Access granted");
                            responseObserver.onNext(Empty.newBuilder().build());
                            responseObserver.onCompleted();
                        } else {
                            Logger.info("Access denied, robot is already being fixed");
                            AccessRequestWrapper wrapper = new AccessRequestWrapper(responseObserver, request.getId());
                            queue.addRobot(wrapper);
                        }
                    }
                }
            }
        }

        @Override
        public void leaveNetwork(RobotServiceOuterClass.LeaveNetworkRequest request, StreamObserver<Empty> responseObserver) {
            Logger.info("Robot " + request.getId() + " left the network in a controlled manner");

            network.removeRobotById(request.getId());

            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }
    }
}
