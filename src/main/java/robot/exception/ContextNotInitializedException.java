package robot.exception;

public class ContextNotInitializedException extends RuntimeException {
    public ContextNotInitializedException(String message) {
        super(message);
    }

    public ContextNotInitializedException(String message, Throwable cause) {
        super(message, cause);
    }
}
