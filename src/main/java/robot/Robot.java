package robot;

import common.bean.RobotInfoBean;
import common.response.RobotInitializationResponse;
import common.utils.Position;
import robot.adapter.RobotInfoAdapter;
import robot.command.CommandExectutor;
import robot.command.CommandScheduler;
import robot.communication.AdministratorRobotClient;
import robot.communication.RobotGRPCClient;
import robot.communication.RobotGRPCServer;
import robot.core.RobotContextProvider;
import robot.core.RobotNetwork;
import robot.core.RobotState;
import robot.exception.ServerRequestException;
import robot.faultDetection.FaultDetectionHandler;
import robot.model.PollutionDataStorage;
import robot.model.RobotInfo;
import robot.task.*;
import utils.Logger;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Robot {

    private RobotNetwork otherRobots;
    private final TaskManager taskManager;
    private final PollutionDataStorage storage;
    private RobotGRPCServer server;
    private final RobotState state;
    private final CommandScheduler scheduler;
    private final CommandExectutor executor;

    public Robot() {
        RobotInfo currentRobot = new RobotInfo();
        RobotNetwork otherRobots = new RobotNetwork();
        String serverAddress = "";
        this.taskManager = new TaskManager();
        this.storage = new PollutionDataStorage();
        this.state = new RobotState();
        this.scheduler = new CommandScheduler();
        this.executor = new CommandExectutor(scheduler, state);
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

        AdministratorRobotClient administratorRobotClient = new AdministratorRobotClient(serverAddress);

        RobotInitializationResponse response = administratorRobotClient.initializeRobot(id, "localhost", port);

        // Initialize robot context
        RobotInfo currentRobot = new RobotInfo(id, port, "localhost", new Position(response.getX(), response.getY()));
        RobotContextProvider.setServerAddress(serverAddress);
        RobotContextProvider.updateCurrentRobot(currentRobot);
        this.otherRobots = new RobotNetwork();

        // Add other robots to the network if any
        if (response.getOtherRobots() != null) {
            for (RobotInfoBean robotInfoBean : response.getOtherRobots()) {
                RobotInfo robotInfo = RobotInfoAdapter.adapt(robotInfoBean);
                this.otherRobots.addRobot(robotInfo);
            }
        }

        Logger.info("Robot initialized successfully");
    }

    private void startSensor() {
        taskManager.addTaskAndStart(new SensorDataReader(storage));
    }

    private void notifyOtherRobots() {
        server = new RobotGRPCServer(RobotContextProvider.getCurrentRobot().getPort(), otherRobots);
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start server", e);
        }
        if (otherRobots.getAllRobots().size() > 0) {
            Logger.info("Notifying other robots");
            for (RobotInfo peerRobot : otherRobots.getAllRobots()) {
                Logger.info("Notifying robot " + peerRobot.getId());
                RobotGRPCClient client = new RobotGRPCClient(peerRobot);
                client.sendRobotInfo(peerRobot);
            }
        }
    }

    private void startPublishing() {
        taskManager.addTaskAndStart(new SensorDataPublisher(storage));
    }

    private void startUserInputReader() {
        taskManager.addTaskAndStart(new UserInputReader(scheduler));
    }

    private void startFaultSimulator() {
        taskManager.addTaskAndStart(new FaultSimulator(scheduler));
    }

    private void startFaultDetectionHandler() {
        taskManager.addTaskAndStart(new FaultDetectionHandler(otherRobots));
    }

    public static void main(String[] args) {
        Logger.info("Starting robot");
        System.out.println("Welcome to the robot application!");

        Robot robot = new Robot();
        robot.state.turnOn();

        try {
            while (true) {
                try {
                    robot.initialize();
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
            robot.startSensor();
            robot.notifyOtherRobots();
            robot.startPublishing();
        } catch (Exception e) {
            System.out.println("Error while starting robot. Please check logs for more details.");
            Logger.error("Error while starting robot: " + e.getMessage());
            Logger.logException(e);
            return;
        }

        robot.startUserInputReader();
        robot.startFaultSimulator();
        robot.startFaultDetectionHandler();

        System.out.println("Robot started successfully\n");

        while (robot.state.isRunning()) {
            robot.executor.execute();
        }

        robot.taskManager.stopAllTasksAndClear();

        AdministratorRobotClient administratorRobotClient = new AdministratorRobotClient();
        administratorRobotClient.unregisterRobot();
        robot.server.stop();

        System.out.println("Robot stopped");

    }
}
