package administrator.validator;

import administrator.exception.InvalidRequestException;
import common.request.RobotInitializationRequest;

public class RobotInitializationRequestValidator {

    public static void validate(RobotInitializationRequest request) throws InvalidRequestException {
        if (request.getId() == null || request.getId().isEmpty()) {
            throw new InvalidRequestException("Robot ID cannot be null or empty");
        }
        if (request.getAddress() == null || request.getAddress().isEmpty()) {
            throw new InvalidRequestException("Robot IP address cannot be null or empty");
        }
        if (request.getPort() < 0 || request.getPort() > 65535) {
            throw new InvalidRequestException("Robot port number must be between 0 and 65535");
        }
    }
}
