package robot.communication;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import robot.RobotServiceGrpc;
import robot.adapter.RobotInfoAdapter;
import robot.model.RobotInfo;
import utils.Logger;

public class RobotGRPCClient {
    private String host;
    private int port;

    public RobotGRPCClient(String host, int port) {
        this.host = host;
        this.port = port;

    }

    public RobotGRPCClient() {
    }

    public void sendRobotInfo(RobotInfo robotInfo) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        RobotServiceGrpc.RobotServiceStub asyncStub = RobotServiceGrpc.newStub(channel);
        asyncStub.sendRobotInfo(RobotInfoAdapter.adapt(robotInfo), new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty response) {
            }

            @Override
            public void onError(Throwable throwable) {
                Logger.error("Error sending robot info to " + robotInfo.getId());
                Logger.logException((Exception) throwable);
            }

            @Override
            public void onCompleted() {
                Logger.debug("Robot info sent to " + robotInfo.getId());
                channel.shutdown();
            }
        });
    }
}
