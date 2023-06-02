package robot.fault.handler;

import com.google.protobuf.Empty;
import common.utils.Logger;
import io.grpc.stub.StreamObserver;

public class AccessRequestWrapper {
    private final StreamObserver<Empty> request;
    private final String id;


    public AccessRequestWrapper(StreamObserver<Empty> request, String id) {
        this.request = request;
        this.id = id;
    }

    public void giveAccess() {
        try {
            request.onNext(Empty.newBuilder().build());
            request.onCompleted();
        } catch (Exception e) {
            Logger.warning("Error giving access to robot");
        }
    }

    public String getId() {
        return id;
    }
}
