package robot.communication;

import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import robot.RobotServiceGrpc;
import robot.RobotServiceOuterClass;
import robot.adapter.RobotPeerAdapter;
import robot.network.RobotNetwork;
import robot.network.RobotNetworkProvider;
import utils.Logger;

public class RobotGRPCServer {
    private final int port;
    private final Server server;

    public RobotGRPCServer(int port) {
        this.port = port;
        this.server = ServerBuilder.forPort(port)
                .addService(new RobotServiceImpl())
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

        public RobotServiceImpl() {
            this.network = RobotNetworkProvider.getNetwork();
        }

        @Override
        public void sendRobotInfo(RobotServiceOuterClass.RobotInfo request, StreamObserver<Empty> responseObserver) {
            Logger.debug("New robot joined the network: " + request.getId());

            network.addRobot(RobotPeerAdapter.adapt(request));

            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }

        @Override
        public void removeRobot(RobotServiceOuterClass.RemoveRobotRequest request, StreamObserver<Empty> responseObserver) {
            Logger.debug("Robot left the network: " + request.getId());

            network.removeRobotById(request.getId());

            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }
    }
}
