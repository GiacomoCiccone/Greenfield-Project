package robot.model;

import java.util.ArrayList;
import java.util.List;

public class RobotNetwork {
    private final List<RobotInfo> otherRobots;

    public RobotNetwork() {
        otherRobots = new ArrayList<>();
    }

    public synchronized void addRobot(RobotInfo robot) {
        otherRobots.add(robot);
    }

    public synchronized RobotInfo getRobotById(String id) {
        for (RobotInfo robot : otherRobots) {
            if (robot.getId().equals(id)) {
                return robot;
            }
        }
        return null;
    }

    public synchronized List<RobotInfo> getAllRobots() {
        return new ArrayList<>(otherRobots);
    }

    public synchronized void removeRobotById(String id) {
        for (RobotInfo robot : otherRobots) {
            if (robot.getId().equals(id)) {
                otherRobots.remove(robot);
                return;
            }
        }
    }

    @Override
    public String toString() {
        return "RobotNetwork{" +
                "otherRobots=" + otherRobots +
                '}';
    }
}
