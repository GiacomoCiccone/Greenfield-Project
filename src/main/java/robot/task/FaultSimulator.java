package robot.task;

import robot.command.CommandScheduler;

import java.util.Random;

public class FaultSimulator extends RobotTaskBase {
    public static final double FAULT_PROBABILITY = 0.1;
    public static final int FAULT_INTERVAL = 10000;
    private final CommandScheduler scheduler;
    private final Random random;

    public FaultSimulator(CommandScheduler scheduler) {
        super();
        this.random = new Random();
        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        while (isRunning()) {
            try {
                Thread.sleep(FAULT_INTERVAL);
            } catch (InterruptedException e) {
                break;
            }
            if (random.nextDouble() < FAULT_PROBABILITY) {
                scheduler.schedule("fix-automatic");
            }
        }
    }
}
