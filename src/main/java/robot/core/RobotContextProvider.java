package robot.core;

import robot.model.RobotInfo;

public class RobotContextProvider {
    private static final RobotInfo currentRobot = new RobotInfo();
    private static String serverAddress = "";

    private RobotContextProvider() {
    }

    public static synchronized RobotInfo getCurrentRobot() {
        return new RobotInfo(currentRobot);
    }

    public static synchronized void updateCurrentRobot(RobotInfo robot) {
        currentRobot.setId(robot.getId());
        currentRobot.setPort(robot.getPort());
        currentRobot.setAddress(robot.getAddress());
        currentRobot.setPosition(robot.getPosition());
    }

    public static synchronized String getServerAddress() {
        return serverAddress;
    }

    public static synchronized void setServerAddress(String address) {
        serverAddress = address;
    }
}
