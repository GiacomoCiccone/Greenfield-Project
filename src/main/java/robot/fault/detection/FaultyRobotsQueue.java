package robot.fault.detection;

import robot.network.RobotPeer;

import java.util.LinkedList;
import java.util.Queue;

public class FaultyRobotsQueue {
    private final Queue<RobotPeer> queue;
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

    public synchronized void addFaultyRobot(RobotPeer robot) {
        queue.add(robot);
        notifyAll();
    }

    public synchronized RobotPeer getNextFaultyRobot() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.poll();
    }

    public synchronized boolean hasFaultyRobots() {
        return !queue.isEmpty();
    }
}
