package robot.communication;

import common.utils.Logger;
import io.grpc.*;
import robot.state.RobotState;
import robot.state.RobotStateProvider;

import java.util.concurrent.Executor;

public class LamportClientMiddleware implements ClientInterceptor {

    private static final Metadata.Key<String> TIMESTAMP_KEY =
            Metadata.Key.of("timestamp", Metadata.ASCII_STRING_MARSHALLER);

    private final RobotState state;

    public LamportClientMiddleware() {
        state = RobotStateProvider.getState();
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                updateTimestamp(headers);

                ClientCall.Listener<RespT> listener = new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onHeaders(Metadata headers) {
                        String timestampString = headers.get(TIMESTAMP_KEY);
                        Logger.info("Headers received from server: " + headers);
                        if (timestampString != null) {
                            try {
                                long receivedTime = Long.parseLong(timestampString);
                                state.updateClock(receivedTime);
                            } catch (NumberFormatException e) {
                                Logger.warning("Invalid timestamp format in headers");
                            }
                        }
                        super.onHeaders(headers);
                    }
                };
                super.start(listener, headers);
            }
        };
    }

    private void updateTimestamp(Metadata headers) {
        synchronized (state) {
            state.updateClock(0);
            headers.put(TIMESTAMP_KEY, String.valueOf(state.getTime()));
        }
    }
}
