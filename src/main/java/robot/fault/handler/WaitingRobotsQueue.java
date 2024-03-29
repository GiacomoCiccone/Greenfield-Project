package robot.fault.handler;

import common.utils.Logger;

import java.util.LinkedList;
import java.util.Queue;

public class WaitingRobotsQueue {
    private final Queue<AccessRequestWrapper> queue;

    private static WaitingRobotsQueue instance;

    private WaitingRobotsQueue() {
        queue = new LinkedList<>();
    }

    public static synchronized WaitingRobotsQueue getQueue() {
        if (instance == null) {
            instance = new WaitingRobotsQueue();
        }
        return instance;
    }

    public synchronized void addRobot(AccessRequestWrapper robot) {
        queue.add(robot);
    }

    public synchronized AccessRequestWrapper getNextRobot() {
        return queue.poll();
    }

    public synchronized boolean hasRobots() {
        return !queue.isEmpty();
    }

    public synchronized void clear() {
        queue.clear();
    }

    public synchronized void removeIfPresentById(String id) {
        Logger.info("Removing robot " + id + " from faulty robots queue");
        queue.removeIf(request -> request.getId().equals(id));
    }
}
