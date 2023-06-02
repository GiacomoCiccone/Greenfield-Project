package robot.fault.detection;

import common.utils.Logger;
import robot.network.RobotInfo;

import java.util.LinkedList;
import java.util.Queue;

public class FaultyRobotsQueue {
    private final Queue<RobotInfo> queue;
    private static FaultyRobotsQueue instance;

    private FaultyRobotsQueue() {
        queue = new LinkedList<>();
    }

    public static synchronized FaultyRobotsQueue getQueue() {
        if (instance == null) {
            instance = new FaultyRobotsQueue();
        }
        return instance;
    }

    public synchronized void addFaultyRobot(RobotInfo robot) {
        Logger.info("Adding robot " + robot.getId() + " to faulty robots queue");
        queue.add(robot);
        notifyAll();
    }

    public synchronized RobotInfo getNextFaultyRobot() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.poll();
    }

    public synchronized boolean hasFaultyRobots() {
        return !queue.isEmpty();
    }
}
