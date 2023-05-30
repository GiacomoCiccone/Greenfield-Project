package robot.communication;

import io.grpc.*;
import robot.faultDetection.FaultyRobotsQueue;
import robot.model.RobotInfo;
import utils.Logger;

public class TimeoutMiddleware implements ClientInterceptor {
    public RobotInfo receiver;

    public TimeoutMiddleware(RobotInfo receiver) {
        this.receiver = receiver;
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
                            Logger.debug("Adding faulty robot to queue: " + receiver.getId());
                            FaultyRobotsQueue.getQueue().addFaultyRobot(receiver);
                        }
                        super.onClose(status, trailers);
                    }
                };
                super.start(listener, headers);
            }
        };
    }
}
