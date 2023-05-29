package robot.task;

import robot.command.CommandScheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInputReader extends RobotTaskBase {
    CommandScheduler scheduler;
    private final BufferedReader reader;

    public UserInputReader(CommandScheduler scheduler) {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        while (isRunning()) {

            scheduler.schedule("sync-input");

            if (!isRunning()) {
                break;
            }

            try {

                // Clean the buffer
                while (reader.ready()) {
                    reader.readLine();
                }

                // Wait for user to press enter. Interruptible scanner.
                while (!reader.ready()) {
                    Thread.sleep(200);
                }

                String input = reader.readLine().toLowerCase();
                scheduler.schedule(input);
            } catch (IOException | InterruptedException e) {
                break;
            }
        }
    }
}
