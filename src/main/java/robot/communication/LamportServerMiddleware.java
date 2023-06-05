package robot.communication;

import common.utils.Logger;
import io.grpc.*;
import robot.state.RobotState;
import robot.state.RobotStateProvider;

public class LamportServerMiddleware implements ServerInterceptor {

    private static final Metadata.Key<String> TIMESTAMP_KEY =
            Metadata.Key.of("timestamp", Metadata.ASCII_STRING_MARSHALLER);

    private final RobotState state;

    public LamportServerMiddleware() {
        state = RobotStateProvider.getState();
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata requestHeaders,
            ServerCallHandler<ReqT, RespT> next) {
        Logger.info("Headers received from client: " + requestHeaders);

        String timestampString = requestHeaders.get(TIMESTAMP_KEY);
        if (timestampString != null) {
            try {
                long receivedTime = Long.parseLong(timestampString);
                state.updateClock(receivedTime);
            } catch (NumberFormatException e) {
                Logger.warning("Invalid timestamp format in headers");
            }
        }

        Metadata responseHeaders = new Metadata();
        updateTimestamp(responseHeaders);

        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void sendHeaders(Metadata headers) {
                headers.merge(responseHeaders);
                super.sendHeaders(headers);
            }
        }, requestHeaders);
    }

    private void updateTimestamp(Metadata headers) {
        synchronized (state) {
            state.updateClock(0);
            headers.put(TIMESTAMP_KEY, String.valueOf(state.getTime()));
        }
    }
}
