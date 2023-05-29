package administrator.server.storage;

import administrator.server.model.RobotEntity;

import java.util.ArrayList;
import java.util.List;

public class RobotStorage {
    private final List<RobotEntity> robotData = new ArrayList<RobotEntity>();

    private boolean lockAcquired = false;
    private final Object lock = new Object();
    private static final RobotStorage instance = new RobotStorage();

    private RobotStorage() {
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
}
