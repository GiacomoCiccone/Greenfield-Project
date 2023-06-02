package robot.fault.detection;

import robot.communication.AdministratorRobotClient;
import robot.communication.RobotGRPCClient;
import robot.exception.ServerRequestException;
import robot.network.RobotNetwork;
import robot.network.RobotNetworkProvider;
import robot.network.RobotInfo;
import common.utils.Logger;

public class FaultDetectionHandler extends Thread {
    private final RobotNetwork network;
    private boolean running;
    private final FaultyRobotsQueue queue;

    public FaultDetectionHandler() {
        this.network = RobotNetworkProvider.getNetwork();
        this.queue = FaultyRobotsQueue.getQueue();
    }

    public void handleFaultyRobot(RobotInfo deadRobotInfo) {
        Logger.info("Handling faulty robot: " + deadRobotInfo.getId());

        if (network.hasRobotWithId(deadRobotInfo.getId())) {
            network.removeRobotById(deadRobotInfo.getId());
        }

        if (network.hasRobots()) {
            for (RobotInfo peer : network.getAllRobots()) {
                RobotGRPCClient client = new RobotGRPCClient(peer);
                Logger.info("Sending faulty robot info to peer: " + peer.getId());
                client.removeRobot(deadRobotInfo.getId());
            }
        }

        AdministratorRobotClient adminClient = new AdministratorRobotClient();

        try {
            adminClient.removeRobot(deadRobotInfo.getId());
        } catch (ServerRequestException e) {
            Logger.warning(e.getMessage());
        }
    }

    @Override
    public void run() {
        Logger.info("Fault detection handler started");
        while (running || queue.hasFaultyRobots()) {
            try {
                RobotInfo faultyRobot = queue.getNextFaultyRobot();

                handleFaultyRobot(faultyRobot);

            } catch (InterruptedException e) {
                Logger.warning("Fault detection handler interrupted");
            }
        }
        Logger.info("Fault detection handler stopped");
    }

    @Override
    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        super.start();
    }

    public synchronized void stopSignal() throws InterruptedException {
        running = false;
        interrupt();
        join();
    }
}
