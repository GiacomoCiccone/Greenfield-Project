package administrator.client.logic;

import administrator.client.Exception.ResponseException;
import common.bean.RobotInfoBean;
import common.response.AveragePollutionValueResponse;
import common.utils.Greenfield;
import common.utils.Position;
import utils.Logger;

import java.util.List;

public class CommandExecutor {
    private final RobotAPIClient robotApiClient;
    private final PollutionAPIClient pollutionApiClient;
    private String lastCommand;

    public CommandExecutor() {
        this.robotApiClient = new RobotAPIClient();
        this.pollutionApiClient = new PollutionAPIClient();
        lastCommand = "";
    }

    public String executeCommand(String command) {
        Logger.debug("Executing command: " + command);

        if (command.equals("last")) {
            if (lastCommand.isEmpty()) {
                return "No previous command";
            } else {
                command = lastCommand;
            }
        } else {
            lastCommand = command;
        }

        if (command.startsWith("show")) {
            return showAllRobots(command);
        } else if (command.startsWith("get")) {
            return processGetCommand(command);
        } else if (command.equalsIgnoreCase("help")) {
            return getHelpMessage();
        } else if (command.equalsIgnoreCase("exit")) {
            return "Exiting the API Client...";
        } else {
            Logger.debug("Invalid command: " + command);
            return "Invalid command: " + command;
        }
    }

    private String showAllRobots(String command) {
        String[] parts = command.split(" ");

        if (parts.length != 2 || !parts[1].equals("--all")) {
            return "Invalid command: " + String.join(" ", parts);
        }

        List<RobotInfoBean> robots = null;
        try {
            robots = robotApiClient.getAllRobots();
        } catch (ResponseException e) {
            Logger.warning("Error getting robot list: " + e.getMessage());
            return "Error getting robot list: " + e.getMessage();
        }

        StringBuilder sb = new StringBuilder();
        for (RobotInfoBean robot : robots) {
            if (robot.getId().equals(robots.get(robots.size() - 1).getId())) {
                sb.append("***************************\n")
                        .append("       Robot ID: ").append(robot.getId()).append("\n")
                        .append("***************************\n")
                        .append("* Position: (").append(robot.getX()).append(", ")
                        .append(robot.getY()).append(")        *\n")
                        .append("* District: ").append(Greenfield.getDistrictFromPosition(new Position(robot.getX(), robot.getY()))).append("             *\n")
                        .append("***************************");
                break;
            }
            sb.append("***************************\n")
                    .append("       Robot ID: ").append(robot.getId()).append("\n")
                    .append("***************************\n")
                    .append("* Position: (").append(robot.getX()).append(", ")
                    .append(robot.getY()).append(")        *\n")
                    .append("* District: ").append(Greenfield.getDistrictFromPosition(new Position(robot.getX(), robot.getY()))).append("             *\n")
                    .append("***************************\n");
        }
        return sb.toString();
    }

    private String processGetCommand(String command) {
        String[] parts = command.split(" ");

        if (parts.length == 4 && parts[1].equals("--last")) {
            String n = parts[2];
            String robotId = parts[3];

            try {
                Integer.parseInt(n);
            } catch (NumberFormatException e) {
                Logger.warning("Error: n must be in range [0, 2^31 - 1]");
                return "Error: n must be in range [0, 2^31 - 1]";
            }

            AveragePollutionValueResponse averageResponse = null;
            try {
                averageResponse = pollutionApiClient.getLastPollutionAverages(n, robotId);
            } catch (ResponseException e) {
                Logger.warning("Error retrieving pollution data: " + e.getMessage());
                return "Error retrieving pollution data: " + e.getMessage();
            }

            if (averageResponse != null) {
                return "Results: " +
                        "\n\t- Average value: " + averageResponse.getValue() +
                        "\n\t- Total samples: " + (int) averageResponse.getTotalSample();
            } else {
                Logger.warning("Error retrieving pollution data.");
                return "Error retrieving pollution data.";
            }
        } else if (parts.length == 4 && parts[1].equals("--interval")) {
            String t1 = parts[2];
            String t2 = parts[3];

            try {
                Long.parseLong(t1);
                Long.parseLong(t2);
            } catch (NumberFormatException e) {
                Logger.warning("Error: t1 and t2 must be in range [0, 2^63 - 1]");
                return "Error: t1 and t2 must be in range [0, 2^63 - 1]";
            }

            AveragePollutionValueResponse averageResponse = null;
            try {
                averageResponse = pollutionApiClient.getPollutionDataByTimestamp(t1, t2);
            } catch (ResponseException e) {
                Logger.warning("Error retrieving pollution data: " + e.getMessage());
                return "Error retrieving pollution data: " + e.getMessage();
            }

            if (averageResponse != null) {
                return "Results: " +
                        "\n\t- Average value: " + averageResponse.getValue() +
                        "\n\t- Total samples: " + (int) averageResponse.getTotalSample();
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
                "- last: Repeat last command\n" +
                "- exit: Quit the program";
    }
}
