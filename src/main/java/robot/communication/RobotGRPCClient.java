package robot.communication;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import robot.RobotServiceGrpc;
import robot.RobotServiceOuterClass;
import robot.adapter.RobotInfoAdapter;
import robot.model.RobotInfo;
import utils.Logger;

public class RobotGRPCClient {
    RobotInfo receiver;

    public RobotGRPCClient(RobotInfo receiver) {
        this.receiver = receiver;
    }

    public void sendRobotInfo(RobotInfo robotInfo) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(receiver.getAddress(), receiver.getPort())
                .intercept(new TimeoutMiddleware(receiver))
                .usePlaintext()
                .build();

        RobotServiceGrpc.RobotServiceStub asyncStub = RobotServiceGrpc.newStub(channel);
        asyncStub.withDeadlineAfter(300, java.util.concurrent.TimeUnit.MILLISECONDS)
                .sendRobotInfo(RobotInfoAdapter.adapt(robotInfo), new StreamObserver<Empty>() {
                    @Override
                    public void onNext(Empty response) {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Logger.warning("Error sending robot info to " + robotInfo.getId());
                        Logger.logException((Exception) throwable);
                        channel.shutdown();
                    }

                    @Override
                    public void onCompleted() {
                        Logger.debug("Robot info sent to " + robotInfo.getId());
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
        asyncStub.withDeadlineAfter(300, java.util.concurrent.TimeUnit.MILLISECONDS)
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
