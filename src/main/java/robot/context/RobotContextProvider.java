package robot.context;

import common.utils.Position;
import robot.exception.ContextAlreadyInitializedException;
import robot.exception.ContextNotInitializedException;

public class RobotContextProvider {
    private static volatile RobotContext context;

    private RobotContextProvider() {
    }

    public static RobotContext getContext() {
        if (context == null) {
            throw new ContextNotInitializedException("Robot context not initialized");
        }
        return context;
    }

    public static synchronized void initializeContext(String id, String address, int port, Position position) throws ContextAlreadyInitializedException {
        if (context != null) {
            throw new ContextAlreadyInitializedException("Robot context already initialized");
        }
        context = new RobotContext(id, address, port, position);
    }
}
