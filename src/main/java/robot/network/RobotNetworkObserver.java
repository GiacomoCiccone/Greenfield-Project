package robot.network;

public interface RobotNetworkObserver {
    void robotAdded(RobotPeer robot);

    void robotRemoved(RobotPeer robot);
}
