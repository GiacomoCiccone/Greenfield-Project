package robot.fault.detection;

import robot.communication.AdministratorRobotClient;
import robot.communication.RobotGRPCClient;
import robot.exception.ServerRequestException;
import robot.network.RobotNetwork;
import robot.network.RobotNetworkProvider;
import robot.network.RobotPeer;
import utils.Logger;

public class FaultDetectionHandler extends Thread {
    private final RobotNetwork network;
    private boolean running;
    private final FaultyRobotsQueue queue;

    public FaultDetectionHandler() {
        this.network = RobotNetworkProvider.getNetwork();
        this.queue = FaultyRobotsQueue.getQueue();
    }

    public void handleFaultyRobot(RobotPeer deadRobotPeer) {
        Logger.info("Handling faulty robot: " + deadRobotPeer.getId());

        if (network.hasRobotWithId(deadRobotPeer.getId())) {
            network.removeRobotById(deadRobotPeer.getId());
        }

        if (network.hasRobots()) {
            for (RobotPeer peer : network.getAllRobots()) {
                RobotGRPCClient client = new RobotGRPCClient(peer);
                Logger.info("Sending faulty robot info to peer: " + peer.getId());
                client.removeRobot(deadRobotPeer.getId());
            }
        }

        AdministratorRobotClient adminClient = new AdministratorRobotClient();

        try {
            adminClient.removeRobot(deadRobotPeer.getId());
        } catch (ServerRequestException e) {
            Logger.warning(e.getMessage());
        }
    }

    @Override
    public void run() {
        Logger.info("Fault detection handler started");
        while (running || queue.hasFaultyRobots()) {
            try {
                RobotPeer faultyRobot = queue.getNextFaultyRobot();

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

    public synchronized void stopSignal() {
        running = false;
        interrupt();
    }
}
