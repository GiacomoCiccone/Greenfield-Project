package robot;

import common.bean.RobotInfoBean;
import common.response.RobotInitializationResponse;
import common.utils.Position;
import robot.adapter.RobotPeerAdapter;
import robot.command.CommandExecutor;
import robot.command.CommandScheduler;
import robot.communication.AdministratorRobotClient;
import robot.communication.RobotGRPCClient;
import robot.communication.RobotGRPCServer;
import robot.context.RobotContextProvider;
import robot.exception.ContextAlreadyInitializedException;
import robot.exception.ServerRequestException;
import robot.fault.detection.FaultDetectionHandler;
import robot.network.RobotNetwork;
import robot.network.RobotNetworkProvider;
import robot.network.RobotPeer;
import robot.state.RobotState;
import robot.state.RobotStateProvider;
import robot.state.StateType;
import robot.task.*;
import common.utils.Logger;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Robot {
    private final TaskManager taskManager;
    private RobotGRPCServer server;
    private final CommandExecutor executor;
    private final RobotState state;
    private final FaultDetectionHandler faultDetectionHandler;

    public Robot() {
        CommandScheduler scheduler = new CommandScheduler();
        this.taskManager = new TaskManager(scheduler);
        this.executor = new CommandExecutor(scheduler, taskManager);
        this.state = RobotStateProvider.getState();
        this.faultDetectionHandler = new FaultDetectionHandler();
    }

    public void initialize() throws ServerRequestException {
        Logger.info("Initializing robot");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter robot ID: ");
        String id = scanner.nextLine();

        int port = 0;
        System.out.print("Enter robot port: ");
        while (true) {
            try {
                port = scanner.nextInt();
                scanner.nextLine(); // Consume newline left-over
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
                scanner.nextLine();
                System.out.print("Enter robot port: ");
            }
        }

        System.out.print("Enter server address: ");
        String serverAddress = scanner.nextLine();

        AdministratorRobotClient.setServerAddress(serverAddress);


        AdministratorRobotClient administratorRobotClient = new AdministratorRobotClient();
        RobotInitializationResponse response = administratorRobotClient.initializeRobot(id, "localhost", port);

        try {
            RobotContextProvider.initializeContext(id, "localhost", port, new Position(response.getX(), response.getY()));
        } catch (ContextAlreadyInitializedException e) {
            Logger.warning("Robot context already initialized");
        }

        // Add other robots to the network if any
        if (response.getOtherRobots() != null) {
            RobotNetwork network = RobotNetworkProvider.getNetwork();
            for (RobotInfoBean robotInfoBean : response.getOtherRobots()) {
                RobotPeer robotPeer = RobotPeerAdapter.adapt(robotInfoBean);
                network.addRobot(robotPeer);
            }
        }

        server = new RobotGRPCServer(port);
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start grpc server", e);
        }

        Logger.info("Robot initialized successfully");
    }

    private void notifyOtherRobots() {
        RobotNetwork network = RobotNetworkProvider.getNetwork();
        if (network.hasRobots()) {
            Logger.info("Notifying other robots");
            for (RobotPeer peerRobot : network.getAllRobots()) {
                Logger.info("Notifying robot " + peerRobot.getId());
                RobotGRPCClient client = new RobotGRPCClient(peerRobot);
                client.sendRobotInfo();
            }
        }
    }

    private void notifyOtherRobotsStop() {
        RobotNetwork network = RobotNetworkProvider.getNetwork();
        if (network.hasRobots()) {
            Logger.info("Notifying other robots of stop");
            for (RobotPeer peerRobot : network.getAllRobots()) {
                Logger.info("Notifying robot " + peerRobot.getId());
                RobotGRPCClient client = new RobotGRPCClient(peerRobot);
                client.leaveNetwork();
            }
        }
    }

    public static void main(String[] args) {
        Logger.info("Starting robot");
        System.out.println("Welcome to the robot application!");

        Robot robot = new Robot();

        try {
            while (true) {
                try {
                    robot.initialize();
                    robot.faultDetectionHandler.start();
                    break;
                } catch (ServerRequestException e) {
                    System.out.println("Error while initializing robot: " + e.getMessage());
                    Logger.error("Error while initializing robot: " + e.getMessage());
                    Logger.logException(e);

                    System.out.print("Do you want to retry? (y/n): ");
                    Scanner scanner = new Scanner(System.in);
                    String retry = scanner.nextLine();
                    if (retry.equals("n")) {
                        return;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error while starting robot. Please check logs for more details.");
            Logger.error("Error while starting robot: " + e.getMessage());
            Logger.logException(e);
            return;
        }


        try {
            robot.notifyOtherRobots();
        } catch (Exception e) {
            System.out.println("Error while starting robot. Please check logs for more details.");
            Logger.error("Error while starting robot: " + e.getMessage());
            Logger.logException(e);
            return;
        }

        robot.taskManager.startTasks();

        System.out.println("Robot started successfully\n");

        // Robot running
        robot.state.turnOn();

        while (robot.state.getState() != StateType.OFF) {
            robot.executor.execute();
        }

        robot.taskManager.stopAllTasksAndClear();

        AdministratorRobotClient administratorRobotClient = new AdministratorRobotClient();
        try {
            administratorRobotClient.unregisterSelf(RobotContextProvider.getContext().getId());
        } catch (ServerRequestException e) {
            Logger.warning("Failed to unregister robot: " + e.getMessage());
        }

        robot.notifyOtherRobotsStop();

        robot.server.stop();
        robot.faultDetectionHandler.stopSignal();
        try {
            robot.faultDetectionHandler.join(); // Wait for fault detection handler to stop
        } catch (InterruptedException e) {
            Logger.warning("Interrupted while waiting for fault detection handler to stop");
        }
        System.out.println("Robot stopped");

    }
}
