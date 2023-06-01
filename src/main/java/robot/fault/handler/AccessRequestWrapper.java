package robot.fault.handler;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;

public class AccessRequestWrapper {
    private final StreamObserver<Empty> request;


    public AccessRequestWrapper(StreamObserver<Empty> request) {
        this.request = request;
    }

    public void giveAccess() {
        request.onNext(Empty.newBuilder().build());
        request.onCompleted();
    }
}
