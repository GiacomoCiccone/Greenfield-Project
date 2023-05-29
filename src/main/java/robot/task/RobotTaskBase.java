package robot.task;

public abstract class RobotTaskBase implements RobotTask {
    private final Thread taskThread;
    private boolean running;

    public RobotTaskBase() {
        this.taskThread = new Thread(this);
        this.running = false;
    }

    @Override
    public void start() {
        running = true;
        if (taskThread != null) {
            taskThread.start();
        }
    }

    @Override
    public void stop() {
        running = false;
        if (taskThread != null) {
            taskThread.interrupt();
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
