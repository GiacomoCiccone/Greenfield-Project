package robot;

import common.bean.RobotInfoBean;
import common.response.RobotInitializationResponse;
import common.utils.Position;
import robot.adapter.RobotInfoAdapter;
import robot.communication.RobotServerClient;
import robot.exception.ServerRequestException;
import robot.model.PollutionDataStorage;
import robot.model.RobotInfo;
import robot.model.RobotNetwork;
import robot.task.SensorDataPublisher;
import robot.task.SensorDataReader;
import robot.task.TaskManager;
import utils.Logger;

import java.util.Scanner;

public class Robot {

    private RobotInfo currentRobot;
    private RobotNetwork otherRobots;
    private String serverAddress;
    private TaskManager taskManager;
    private PollutionDataStorage storage;

    public Robot() {
        RobotInfo currentRobot = new RobotInfo();
        RobotNetwork otherRobots = new RobotNetwork();
        String serverAddress = "";
        this.taskManager = new TaskManager();
        this.storage = new PollutionDataStorage();
    }

    public void initialize() throws ServerRequestException {
        Logger.info("Initializing robot");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter robot ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter robot port: ");
        int port = scanner.nextInt();
        scanner.nextLine(); // Consume newline left-over

        System.out.print("Enter server address: ");
        String serverAddress = scanner.nextLine();

        RobotServerClient robotServerClient = new RobotServerClient(serverAddress);

        RobotInitializationResponse response = robotServerClient.initializeRobot(id, "localhost", port);

        this.serverAddress = serverAddress;
        this.currentRobot = new RobotInfo(id, port, "localhost", new Position(response.getX(), response.getY()));
        this.otherRobots = new RobotNetwork();

        System.out.println(response.getOtherRobots());

        if (response.getOtherRobots() != null) {
            for (RobotInfoBean robotInfoBean : response.getOtherRobots()) {
                RobotInfo robotInfo = RobotInfoAdapter.adapt(robotInfoBean);
                this.otherRobots.addRobot(robotInfo);
            }
        }

        Logger.info("Robot initialized successfully");
    }

    private void startSensor() {
        Logger.info("Starting sensor data reader");
        taskManager.addTaskAndStart(new SensorDataReader(storage));
    }

    private void notifyOtherRobots() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void startPublishing() {
        Logger.info("Starting publisher");
        taskManager.addTaskAndStart(new SensorDataPublisher(currentRobot, storage));
    }

    public static void main(String[] args) {
        Logger.info("Starting robot");
        System.out.println("Welcome to the robot application!");

        Robot robot = new Robot();

        try {
            robot.initialize();
        } catch (ServerRequestException e) {
            System.out.println("Error while initializing robot. Please check logs for more details.");
            Logger.error("Error while initializing robot: " + e.getMessage());
            return;
        }

        robot.startSensor();
        //robot.notifyOtherRobots();
        robot.startPublishing();

        System.out.println("Press enter to stop the robot");
        Scanner scanner = new Scanner(System.in);

        scanner.nextLine();

        robot.taskManager.stopAllTasksAndClear();

        RobotServerClient robotServerClient = new RobotServerClient(robot.serverAddress);
        robotServerClient.unregisterRobot(robot.currentRobot.getId());

        System.out.println("Robot stopped");

    }

}
