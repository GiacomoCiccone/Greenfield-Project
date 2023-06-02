package robot.network;

public interface RobotNetworkObserver {
    void robotAdded(RobotInfo robot);

    void robotRemoved(RobotInfo robot);
}
