package robot.state;

public interface RobotStateObserver {
    void onStateChanged(StateType newState);
}
