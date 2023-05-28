package administrator.server.validator;

import administrator.exception.InvalidParametersException;
import common.request.RobotInitializationRequest;

public class RobotInitializationRequestValidator {

    public static void validate(RobotInitializationRequest request) throws InvalidParametersException {
        if (request.getId() == null || request.getId().isEmpty()) {
            throw new InvalidParametersException("Robot ID cannot be null or empty");
        }
        if (request.getAddress() == null || request.getAddress().isEmpty()) {
            throw new InvalidParametersException("Robot IP address cannot be null or empty");
        }
        if (request.getPort() < 0 || request.getPort() > 65535) {
            throw new InvalidParametersException("Robot port number must be between 0 and 65535");
        }
    }
}
