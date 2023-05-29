package robot.communication;

import com.google.protobuf.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import robot.RobotServiceGrpc;
import robot.RobotServiceOuterClass;
import robot.adapter.RobotInfoAdapter;
import robot.model.RobotNetwork;
import utils.Logger;

public class RobotGRPCServer {
    private final int port;
    private final Server server;

    public RobotGRPCServer(int port, RobotNetwork network) {
        this.port = port;
        this.server = ServerBuilder.forPort(port)
                .addService(new RobotServiceImpl(network))
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

        public RobotServiceImpl(RobotNetwork network) {
            this.network = network;
        }

        @Override
        public void sendRobotInfo(RobotServiceOuterClass.RobotInfo request, StreamObserver<Empty> responseObserver) {
            Logger.debug("New robot joined the network: " + request.getId());

            network.addRobot(RobotInfoAdapter.adapt(request));

            System.out.println(network);

            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }
    }
}
