package robot.network;

import common.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class RobotNetwork {
    private final List<RobotPeer> robots;
    private final List<RobotNetworkObserver> observers;

    RobotNetwork() {
        this.robots = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public synchronized void addRobot(RobotPeer robot) {
        Logger.info("Adding robot to network: " + robot.getId());
        robots.add(robot);
        observers.forEach(observer -> observer.robotAdded(new RobotPeer(robot)));

        Logger.info("Robot network's size: " + robots.size());
    }

    public synchronized void removeRobot(RobotPeer robot) {
        if (robots.remove(robot)) {
            Logger.info("Removing robot from network: " + robot.getId());
            observers.forEach(observer -> observer.robotRemoved(new RobotPeer(robot)));
        }
        Logger.info("Robot network's size: " + robots.size());
    }

    public synchronized void removeRobotById(String id) {
        for (RobotPeer robot : robots) {
            if (robot.getId().equals(id)) {
                removeRobot(robot);
                return;
            }
        }
    }

    public synchronized RobotPeer getRobotById(String id) {
        for (RobotPeer robot : robots) {
            if (robot.getId().equals(id)) {
                return new RobotPeer(robot);
            }
        }
        return null;
    }

    public synchronized List<RobotPeer> getAllRobots() {
        // deep copy
        List<RobotPeer> result = new ArrayList<>();
        for (RobotPeer robot : robots) {
            result.add(new RobotPeer(robot));
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
        for (RobotPeer robot : robots) {
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
