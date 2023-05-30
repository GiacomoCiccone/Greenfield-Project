package robot.faultDetection;

import administrator.client.Client;
import robot.communication.AdministratorRobotClient;
import robot.communication.RobotGRPCClient;
import robot.communication.RobotGRPCServer;
import robot.core.RobotNetwork;
import robot.model.RobotInfo;
import robot.task.RobotTask;
import robot.task.RobotTaskBase;
import utils.Logger;

public class FaultDetectionHandler extends RobotTaskBase {
    RobotNetwork network;

    public FaultDetectionHandler(RobotNetwork network) {
        this.network = network;
    }

    public void handleFaultyRobot(RobotInfo deadRobotInfo) {
        Logger.debug("Handling faulty robot: " + deadRobotInfo.getId());

        if (network.getRobotById(deadRobotInfo.getId()) == null) {
            Logger.debug("Robot not found in network: " + deadRobotInfo.getId());
            return;
        }

        if (network.getAllRobots().size() > 0) {
            for (RobotInfo peer : network.getAllRobots()) {
                RobotGRPCClient client = new RobotGRPCClient(peer);
                Logger.debug("Sending faulty robot info to peer: " + peer.getId());
                client.removeRobot(deadRobotInfo.getId());
            }
        }

        AdministratorRobotClient adminClient = new AdministratorRobotClient();
        adminClient.removeRobot(deadRobotInfo.getId());
    }

    @Override
    public void run() {
        Logger.info("Fault detection handler started");
        while (isRunning()) {
            try {
                RobotInfo faultyRobot = FaultyRobotsQueue.getQueue().getNextFaultyRobot();

                handleFaultyRobot(faultyRobot);

            } catch (InterruptedException e) {
                Logger.warning("Fault detection handler interrupted");
                break;
            }
        }
        Logger.info("Fault detection handler stopped");
    }
}
