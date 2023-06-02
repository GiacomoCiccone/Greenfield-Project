package robot.command;

import robot.fault.handler.AccessControlAlgorithm;
import robot.state.RobotState;
import robot.state.RobotStateProvider;
import robot.task.TaskManager;
import common.utils.Logger;

public class CommandExecutor {
    private final CommandScheduler scheduler;
    private final RobotState state;
    private final TaskManager taskManager;

    private static final int COUNTDOWN_STEPS = 1000;
    private static final int COUNTDOWN_SECONDS = 10;

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
            Logger.warning("Error while scheduling command");
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
                System.out.println("Waiting while fixing the robot... ");

                // Mechanic time
                int progress = 0;
                for (int i = COUNTDOWN_STEPS; i > 0; i--) {
                    progress = (COUNTDOWN_STEPS - i) * 100 / COUNTDOWN_STEPS;
                    printProgressBar(progress);
                    try {
                        Thread.sleep(1000 * COUNTDOWN_SECONDS / COUNTDOWN_STEPS);
                    } catch (InterruptedException e) {
                        System.err.println("Interrupted while sleeping");
                    }
                    System.out.print("\r"); // return at the beginning of the line
                    System.out.print("\033[K"); // clear the line

                }

                printProgressBar(100);

                state.leftMechanic();
                algorithm.releaseAccess();

                System.out.println("\nRobot fixed");
                taskManager.startTasks();
                break;
            case "stop":
                System.out.println("\nStopping robot");
                state.turnOff();
                break;
            case "sync-input":
                System.out.print("\nEnter command (type 'help' for help): ");
                break;
            case "help":
                System.out.println("\nAvailable commands: fix, stop, help");
                System.out.println("fix - send the robot to the mechanic");
                System.out.println("stop - stop the robot");
                System.out.println("help - show this help");
                break;
            default:
                System.out.println("\nUnknown command: " + command);
                Logger.error("Unknown command: " + command);
        }
    }

    private static void printProgressBar(int progress) {
        int barWidth = 40;
        int filledWidth = barWidth * progress / 100;
        int emptyWidth = barWidth - filledWidth;

        System.out.print("\r[");
        for (int i = 0; i < filledWidth; i++) {
            System.out.print("=");
        }
        for (int i = 0; i < emptyWidth; i++) {
            System.out.print(" ");
        }
        System.out.print("] " + progress + "%");
        System.out.flush();
    }
}
