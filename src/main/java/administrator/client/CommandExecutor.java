package administrator.client;

import common.bean.RobotInfoBean;
import common.response.AveragePollutionValueResponse;
import common.utils.Greenfield;
import common.utils.Position;
import utils.Logger;

import java.util.List;

public class CommandExecutor {
    private final RobotAPIClient robotApiClient;
    private final PollutionAPIClient pollutionApiClient;

    public CommandExecutor() {
        this.robotApiClient = new RobotAPIClient();
        this.pollutionApiClient = new PollutionAPIClient();
    }

    public String executeCommand(String command) {
        Logger.info("Executing command: " + command);

        if (command.startsWith("show")) {
            return showAllRobots(command);
        } else if (command.startsWith("get")) {
            return processGetCommand(command);
        } else if (command.equalsIgnoreCase("help")) {
            return getHelpMessage();
        } else if (command.equalsIgnoreCase("exit")) {
            return "Exiting the API Client...";
        } else {
            Logger.info("Invalid command: " + command);
            return "Invalid command: " + command;
        }
    }

    private String showAllRobots(String command) {
        String[] parts = command.split(" ");

        if (parts.length != 2 || !parts[1].equals("--all")) {
            return "Invalid command: " + String.join(" ", parts);
        }

        List<RobotInfoBean> robots = robotApiClient.getAllRobots();

        if (robots != null) {
            StringBuilder sb = new StringBuilder();
            for (RobotInfoBean robot : robots) {
                sb.append("***************************\n")
                        .append("*       Robot ID: ").append(robot.getId()).append("       *\n")
                        .append("***************************\n")
                        .append("* Position: (").append(robot.getX()).append(", ")
                        .append(robot.getY()).append(")        *\n")
                        .append("* District: ").append(Greenfield.getDistrictFromPosition(new Position(robot.getX(), robot.getY()))).append("             *\n")
                        .append("***************************\n");
            }
            return sb.toString();
        } else {
            return "Error retrieving robot information.";
        }
    }

    private String processGetCommand(String command) {
        String[] parts = command.split(" ");

        if (parts.length == 4 && parts[1].equals("--last")) {
            String n = parts[2];
            String robotId = parts[3];

            try {
                Integer.parseInt(n);
            } catch (NumberFormatException e) {
                return "Error: n must be in range [0, 2^31 - 1]";
            }

            AveragePollutionValueResponse averageResponse = pollutionApiClient.getLastPollutionAverages(n, robotId);

            if (averageResponse != null) {
                return "Results: " +
                        "\n\t- Average value: " + averageResponse.getValue() +
                        "\n\t- Total samples: " + averageResponse.getTotalSample();
            } else {
                return "Error retrieving pollution data.";
            }
        } else if (parts.length == 4 && parts[1].equals("--interval")) {
            String t1 = parts[2];
            String t2 = parts[3];

            try {
                Long.parseLong(t1);
                Long.parseLong(t2);
            } catch (NumberFormatException e) {
                return "Error: t1 and t2 must be in range [0, 2^63 - 1]";
            }

            AveragePollutionValueResponse averageResponse = pollutionApiClient.getPollutionDataByTimestamp(t1, t2);

            if (averageResponse != null) {
                return "Results: " +
                        "\n\t- Average value: " + averageResponse.getValue() +
                        "\n\t- Total samples: " + averageResponse.getTotalSample();
            } else {
                return "Error retrieving pollution data.";
            }
        } else {
            return "Invalid command: " + command;
        }
    }

    private String getHelpMessage() {
        return "Available commands:\n" +
                "-----------------------------\n" +
                "- show --all: List all robots\n" +
                "- get --last <n> <robotId>: Get last n pollution averages for robot\n" +
                "- get --interval <t1> <t2>: Get pollution data between timestamps\n" +
                "- help: Show available commands\n" +
                "- exit: Quit the program\n";
    }
}