package robot.communication;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import robot.RobotServiceGrpc;
import robot.RobotServiceOuterClass;
import robot.adapter.RobotPeerAdapter;
import robot.network.RobotPeer;
import utils.Logger;

public class RobotGRPCClient {
    RobotPeer receiver;

    public RobotGRPCClient(RobotPeer receiver) {
        this.receiver = receiver;
    }

    public void sendRobotInfo(RobotPeer robotPeer) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(receiver.getAddress(), receiver.getPort())
                .intercept(new TimeoutMiddleware(receiver))
                .usePlaintext()
                .build();

        RobotServiceGrpc.RobotServiceStub asyncStub = RobotServiceGrpc.newStub(channel);
        asyncStub.withDeadlineAfter(1000, java.util.concurrent.TimeUnit.MILLISECONDS)
                .sendRobotInfo(RobotPeerAdapter.adapt(robotPeer), new StreamObserver<Empty>() {
                    @Override
                    public void onNext(Empty response) {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Logger.warning("Error sending robot info to " + robotPeer.getId());
                        Logger.logException((Exception) throwable);
                        channel.shutdown();
                    }

                    @Override
                    public void onCompleted() {
                        Logger.debug("Robot info sent to " + robotPeer.getId());
                        channel.shutdown();
                    }
                });
    }

    public void removeRobot(String deadRobotId) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(receiver.getAddress(), receiver.getPort())
                .intercept(new TimeoutMiddleware(receiver))
                .usePlaintext()
                .build();

        RobotServiceOuterClass.RemoveRobotRequest request = RobotServiceOuterClass.RemoveRobotRequest.newBuilder()
                .setId(deadRobotId)
                .build();

        RobotServiceGrpc.RobotServiceStub asyncStub = RobotServiceGrpc.newStub(channel);
        asyncStub.withDeadlineAfter(1000, java.util.concurrent.TimeUnit.MILLISECONDS)
                .removeRobot(request, new StreamObserver<Empty>() {
                    @Override
                    public void onNext(Empty response) {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Logger.warning("Error sending message to remove robot " + deadRobotId + " to " + receiver.getId());
                        Logger.logException((Exception) throwable);
                        channel.shutdown();
                    }

                    @Override
                    public void onCompleted() {
                        Logger.debug("Message sent to remove robot " + deadRobotId + " to " + receiver.getId());
                        channel.shutdown();
                    }
                });
    }
}
