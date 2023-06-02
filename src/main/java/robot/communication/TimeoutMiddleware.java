package robot.communication;

import io.grpc.*;
import robot.fault.detection.FaultyRobotsQueue;
import robot.network.RobotInfo;
import common.utils.Logger;

public class TimeoutMiddleware implements ClientInterceptor {
    private final RobotInfo receiver;
    private final FaultyRobotsQueue queue;

    public TimeoutMiddleware(RobotInfo receiver) {
        this.receiver = receiver;
        queue = FaultyRobotsQueue.getQueue();
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                ClientCall.Listener<RespT> listener = new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onClose(Status status, Metadata trailers) {
                        if (status.getCode() == Status.Code.DEADLINE_EXCEEDED || status.getCode() == Status.Code.UNAVAILABLE) {
                            Logger.info("Adding faulty robot to queue: " + receiver.getId());
                            queue.addFaultyRobot(receiver);
                        }
                        responseListener.onClose(status, trailers);
                    }
                };
                super.start(listener, headers);
            }
        };
    }
}
