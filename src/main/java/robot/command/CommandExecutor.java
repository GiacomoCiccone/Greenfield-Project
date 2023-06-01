package robot.command;

import robot.fault.handler.AccessControlAlgorithm;
import robot.state.RobotState;
import robot.state.RobotStateProvider;
import robot.task.TaskManager;
import utils.Logger;

public class CommandExecutor {
    private final CommandScheduler scheduler;
    private final RobotState state;
    private final TaskManager taskManager;

    public CommandExecutor(CommandScheduler scheduler, TaskManager taskManager) {
        this.scheduler = scheduler;
        this.state = RobotStateProvider.getState();
        this.taskManager = taskManager;
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
                System.out.println("\nOps, something went wrong, sending the robot to the mechanic...");
            case "fix":
                taskManager.stopAllTasksAndClear();
                System.out.println("Waiting to enter mechanic...");
                state.faultOccurred();

                AccessControlAlgorithm algorithm = new AccessControlAlgorithm();
                algorithm.getAccess();
                state.enteredMechanic();

                System.out.println("Robot entered mechanic");

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Logger.error("Error while sleeping");
                    Logger.logException(e);
                }

                state.leftMechanic();
                algorithm.releaseAccess();

                System.out.println("Robot fixed");
                taskManager.startTasks();
                break;
            case "stop":
                System.out.println("Stopping robot");
                state.turnOff();
                break;
            case "sync-input":
                System.out.print("Enter command (type 'help' for help): ");
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
