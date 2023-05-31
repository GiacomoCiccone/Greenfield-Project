package robot.exception;

public class ContextAlreadyInitializedException extends RuntimeException {

    public ContextAlreadyInitializedException(String message) {
        super(message);
    }

    public ContextAlreadyInitializedException(String message, Throwable cause) {
        super(message, cause);
    }
}
