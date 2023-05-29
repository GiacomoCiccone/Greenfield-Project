package administrator.server.validator;

import administrator.server.exception.InvalidRequestParametersException;
import common.request.RobotInitializationRequest;

public class RobotInitializationRequestValidator {
    public static void validate(RobotInitializationRequest request) throws InvalidRequestParametersException {
        if (request.getId() == null || request.getId().isEmpty()) {
            throw new InvalidRequestParametersException("Robot ID cannot be null or empty");
        }
        if (request.getAddress() == null || request.getAddress().isEmpty()) {
            throw new InvalidRequestParametersException("Robot IP address cannot be null or empty");
        }
        if (request.getPort() < 0 || request.getPort() > 65535) {
            throw new InvalidRequestParametersException("Robot port number must be between 0 and 65535");
        }
    }
}
