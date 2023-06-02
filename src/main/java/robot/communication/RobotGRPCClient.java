package robot.communication;

import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import robot.RobotServiceGrpc;
import robot.RobotServiceOuterClass;
import robot.adapter.RobotInfoConverter;
import robot.context.RobotContextProvider;
import robot.fault.handler.WaitingRobotsQueue;
import robot.network.RobotInfo;
import robot.state.RobotStateProvider;
import common.utils.Logger;

import java.util.List;

public class RobotGRPCClient {
    RobotInfo receiver;

    public RobotGRPCClient(RobotInfo receiver) {
        this.receiver = receiver;
    }

    public void sendRobotInfo() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(receiver.getAddress(), receiver.getPort())
                .intercept(new TimeoutMiddleware(receiver))
                .usePlaintext()
                .build();
        RobotInfo robotInfo = RobotContextProvider.getContext().getRobotInfo();
        RobotServiceGrpc.RobotServiceStub asyncStub = RobotServiceGrpc.newStub(channel);
        asyncStub.withDeadlineAfter(1000, java.util.concurrent.TimeUnit.MILLISECONDS)
                .sendRobotInfo(RobotInfoConverter.convert(robotInfo), new StreamObserver<Empty>() {
                    @Override
                    public void onNext(Empty response) {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Logger.warning("Error sending robot info to " + robotInfo.getId());
                        channel.shutdown();
                    }

                    @Override
                    public void onCompleted() {
                        Logger.info("Robot info sent to " + robotInfo.getId());
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
                        channel.shutdown();
                    }

                    @Override
                    public void onCompleted() {
                        Logger.info("Message sent to remove robot " + deadRobotId + " to " + receiver.getId());
                        channel.shutdown();
                    }
                });
    }

    public void askAccess(List<String> waitingOks) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(receiver.getAddress(), receiver.getPort())
                .intercept(new TimeoutMiddleware(receiver))
                .usePlaintext()
                .build();

        String id = RobotContextProvider.getContext().getId();
        long timestamp = RobotStateProvider.getState().getLastFaultTime();
        RobotServiceOuterClass.AccessRequest request = RobotServiceOuterClass.AccessRequest.newBuilder()
                .setId(id)
                .setTimestamp(timestamp)
                .build();

        RobotServiceGrpc.RobotServiceStub asyncStub = RobotServiceGrpc.newStub(channel);

        int waitingOksSize;
        synchronized (waitingOks) {
            waitingOksSize = waitingOks.size();
        }
        int timeout = waitingOksSize * 10000 + 10000;
        asyncStub.withDeadlineAfter(timeout, java.util.concurrent.TimeUnit.MILLISECONDS)
                .askAccess(request, new StreamObserver<Empty>() {
                    @Override
                    public void onNext(Empty response) {
                        Logger.info("Access granted by " + receiver.getId());
                        synchronized (waitingOks) {
                            waitingOks.remove(receiver.getId());
                            waitingOks.notify();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Logger.warning("Error while asking access to " + receiver.getId());
                        synchronized (waitingOks) {
                            if (waitingOks.remove(receiver.getId())) {
                                waitingOks.notify();
                            }
                        }
                        WaitingRobotsQueue.getQueue().removeIfPresentById(receiver.getId());
                    }

                    @Override
                    public void onCompleted() {
                        channel.shutdown();
                    }
                });
    }

    public void leaveNetwork() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(receiver.getAddress(), receiver.getPort())
                .intercept(new TimeoutMiddleware(receiver))
                .usePlaintext()
                .build();
        RobotInfo robotInfo = RobotContextProvider.getContext().getRobotInfo();
        RobotServiceGrpc.RobotServiceStub asyncStub = RobotServiceGrpc.newStub(channel);

        RobotServiceOuterClass.LeaveNetworkRequest request = RobotServiceOuterClass.LeaveNetworkRequest.newBuilder()
                .setId(robotInfo.getId())
                .build();
        asyncStub.withDeadlineAfter(1000, java.util.concurrent.TimeUnit.MILLISECONDS)
                .leaveNetwork(request, new StreamObserver<Empty>() {
                    @Override
                    public void onNext(Empty response) {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Logger.warning("Error sending robot info to " + robotInfo.getId());
                        channel.shutdown();
                    }

                    @Override
                    public void onCompleted() {
                        Logger.info("Leave network message sent to " + robotInfo.getId());
                        channel.shutdown();
                    }
                });
    }
}
