package robot.faultDetection;

import robot.model.RobotInfo;

import java.util.LinkedList;
import java.util.Queue;

public class FaultyRobotsQueue {
    private final Queue<RobotInfo> faultyQueue;
    private static FaultyRobotsQueue instance;


    private FaultyRobotsQueue() {
        faultyQueue = new LinkedList<>();
    }

    public static synchronized FaultyRobotsQueue getQueue() {
        if (instance == null) {
            instance = new FaultyRobotsQueue();
        }
        return instance;
    }

    public synchronized void addFaultyRobot(RobotInfo robot) {
        faultyQueue.add(robot);
        notifyAll();
    }

    public synchronized RobotInfo getNextFaultyRobot() throws InterruptedException {
        while (faultyQueue.isEmpty()) {
            wait();
        }
        return faultyQueue.poll();
    }
}
