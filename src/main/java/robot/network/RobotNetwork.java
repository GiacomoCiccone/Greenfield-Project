package robot.network;

import common.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class RobotNetwork {
    private final List<RobotInfo> robots;
    private final List<RobotNetworkObserver> observers;

    RobotNetwork() {
        this.robots = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public synchronized void addRobot(RobotInfo robot) {
        Logger.info("Adding robot to network: " + robot.getId());
        robots.add(robot);
        observers.forEach(observer -> observer.robotAdded(new RobotInfo(robot)));

        Logger.info("Robot network's size: " + robots.size());
    }

    public synchronized void removeRobot(RobotInfo robot) {
        if (robots.remove(robot)) {
            Logger.info("Removing robot from network: " + robot.getId());
            observers.forEach(observer -> observer.robotRemoved(new RobotInfo(robot)));
        }
        Logger.info("Robot network's size: " + robots.size());
    }

    public synchronized void removeRobotById(String id) {
        for (RobotInfo robot : robots) {
            if (robot.getId().equals(id)) {
                removeRobot(robot);
                return;
            }
        }
    }

    public synchronized RobotInfo getRobotById(String id) {
        for (RobotInfo robot : robots) {
            if (robot.getId().equals(id)) {
                return new RobotInfo(robot);
            }
        }
        return null;
    }

    public synchronized List<RobotInfo> getAllRobots() {
        // deep copy
        List<RobotInfo> result = new ArrayList<>();
        for (RobotInfo robot : robots) {
            result.add(new RobotInfo(robot));
        }
        return result;
    }

    public synchronized boolean hasRobots() {
        return !robots.isEmpty();
    }

    public synchronized int getRobotsCount() {
        return robots.size();
    }

    public synchronized boolean hasRobotWithId(String id) {
        for (RobotInfo robot : robots) {
            if (robot.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void addObserver(RobotNetworkObserver observer) {
        observers.add(observer);
    }

    public synchronized void removeObserver(RobotNetworkObserver observer) {
        observers.remove(observer);
    }
}
