package robot.state;

public class RobotStateProvider {
    private static final RobotState instance = new RobotState();

    public static RobotState getState() {
        return instance;
    }
}
