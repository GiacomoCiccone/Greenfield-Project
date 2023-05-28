package robot;

import common.bean.RobotInfoBean;
import common.response.RobotInitializationResponse;
import common.utils.Position;
import robot.adapter.RobotInfoAdapter;
import robot.communication.RobotServerClient;
import robot.exception.ServerRequestException;
import robot.model.RobotInfo;
import robot.model.RobotNetwork;
import utils.Logger;

import java.util.Scanner;

public class Robot {

    private RobotInfo currentRobot;
    private RobotNetwork otherRobots;
    private String serverAddress;

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

        for (RobotInfoBean robotInfoBean : response.getOtherRobots()) {
            RobotInfo robotInfo = RobotInfoAdapter.adapt(robotInfoBean);
            this.otherRobots.addRobot(robotInfo);
        }

        Logger.info("Robot initialized successfully");
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

        System.out.println("Robot initialized successfully!");


    }
}
