package robot.network;

public class RobotNetworkProvider {
    private static final RobotNetwork instance = new RobotNetwork();

    public synchronized static RobotNetwork getNetwork() {
        return instance;
    }
}
