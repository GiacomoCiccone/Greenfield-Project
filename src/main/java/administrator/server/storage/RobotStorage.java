package administrator.server.storage;

import administrator.server.model.RobotEntity;

import java.util.ArrayList;
import java.util.List;

public class RobotStorage {
    private final List<RobotEntity> robotData = new ArrayList<>();
    private boolean lockAcquired = false;
    private final Object lock = new Object();
    private static final RobotStorage instance = new RobotStorage();
    private volatile boolean running;
    private Runnable runnable;
    private Thread thread;

    private RobotStorage() {
        running = false;
    }

    public static synchronized RobotStorage getInstance() {
        return instance;
    }

    public List<RobotEntity> getRobotData() {
        return robotData;
    }

    public Object getLock() {
        return lock;
    }

    public boolean getLockAcquired() {
        return lockAcquired;
    }

    public void setLockAcquired(boolean lockAcquired) {
        this.lockAcquired = lockAcquired;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        if (running) {
            throw new IllegalStateException("Cannot set Runnable while it is running.");
        }
        this.runnable = runnable;
    }

    public synchronized void startRunnable() {
        if (runnable != null && !running) {
            running = true;
            thread = new Thread(runnable);
            thread.start();
        }
    }

    public synchronized void stopRunnable() {
        if (running) {
            running = false;
            if (thread != null) {
                thread.interrupt();
                thread = null;
            }
        }
    }
}
