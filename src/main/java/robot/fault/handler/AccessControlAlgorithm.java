package robot.fault.handler;

import robot.communication.RobotGRPCClient;
import robot.network.RobotNetwork;
import robot.network.RobotNetworkObserver;
import robot.network.RobotNetworkProvider;
import robot.network.RobotInfo;
import common.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class AccessControlAlgorithm implements RobotNetworkObserver {
    private final WaitingRobotsQueue queue;
    private final List<String> waitingOks;
    private final RobotNetwork network;

    public AccessControlAlgorithm() {
        this.queue = WaitingRobotsQueue.getQueue();
        this.queue.clear();
        this.network = RobotNetworkProvider.getNetwork();
        this.waitingOks = new ArrayList<>();
        network.addObserver(this);
    }

    public void getAccess() {
        List<RobotInfo> robots = network.getAllRobots();
        if (robots.isEmpty()) {
            return;
        }

        for (RobotInfo robot : robots) {
            synchronized (waitingOks) {
                Logger.info("Robot waiting for access by " + robot.getId());
                waitingOks.add(robot.getId());
            }
        }

        while (true) {
            int size;
            synchronized (waitingOks) {
                size = waitingOks.size();
                if (size == 0) {
                    break;
                }

                for (String peerId : waitingOks) {
                    sendRequest(peerId);
                }

                for (int i = 0; i < size; i++) {
                    try {
                        waitingOks.wait();
                    } catch (InterruptedException e) {
                        Logger.warning("Interrupted while waiting for access");
                    }
                }
            }
        }
    }

    public void releaseAccess() {
        while (queue.hasRobots()) {
            AccessRequestWrapper waitingRobot = queue.getNextRobot();
            waitingRobot.giveAccess();
        }
        network.removeObserver(this);
    }

    private void sendRequest(String peerId) {
        RobotGRPCClient client = new RobotGRPCClient(network.getRobotById(peerId));
        client.askAccess(waitingOks);
    }


    @Override
    public void robotAdded(RobotInfo robot) {
        synchronized (waitingOks) {
            Logger.info("Robot waiting for access by " + robot.getId());
            waitingOks.add(robot.getId());
        }
    }

    @Override
    public void robotRemoved(RobotInfo robot) {
        synchronized (waitingOks) {
            Logger.info("Robot removed from waiting for access by " + robot.getId());
            if (waitingOks.remove(robot.getId())) {
                waitingOks.notify();
            }
        }
        queue.removeIfPresentById(robot.getId());
    }
}
