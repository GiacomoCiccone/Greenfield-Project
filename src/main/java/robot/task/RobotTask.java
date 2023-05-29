package robot.task;

public interface RobotTask extends Runnable {

    void start();

    void stop();

    boolean isRunning();
}
