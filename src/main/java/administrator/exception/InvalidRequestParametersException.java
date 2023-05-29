package administrator.exception;

public class InvalidRequestParametersException extends Exception {

    public InvalidRequestParametersException(String message) {
        super(message);
    }

    public InvalidRequestParametersException(String message, Throwable cause) {
        super(message, cause);
    }
}
