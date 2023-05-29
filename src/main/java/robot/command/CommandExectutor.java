package robot.command;

import robot.Robot;
import utils.Logger;

public class CommandExectutor {
    private final CommandScheduler scheduler;
    private final Robot.RobotState state;

    public CommandExectutor(CommandScheduler scheduler, Robot.RobotState state) {
        this.scheduler = scheduler;
        this.state = state;
    }

    public void execute() {
        try {
            String command = scheduler.getNextCommand();
            executeCommand(command);
            scheduler.clearCommand();
        } catch (InterruptedException e) {
            Logger.error("Error while scheduling command");
            Logger.logException(e);
        }
    }

    private void executeCommand(String command) {
        Logger.info("Executing command: " + command);

        switch (command) {
            case "fix-automatic":
                System.out.println("Ops, something went wrong, sending the robot to the mechanic...");
            case "fix":
                System.out.println("Robot fixed, resuming operation");
                break;
            case "stop":
                System.out.println("Stopping robot");
                state.turnOff();
                break;
            case "sync-input":
                System.out.println("Enter command (type 'help' for help):");
                break;
            case "help":
                System.out.println("Available commands: fix, stop, help");
                System.out.println("fix - send the robot to the mechanic");
                System.out.println("stop - stop the robot");
                System.out.println("help - show this help");
                break;
            default:
                System.out.println("Unknown command: " + command);
                Logger.error("Unknown command: " + command);
        }
    }
}
