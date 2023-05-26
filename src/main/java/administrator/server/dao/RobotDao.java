package administrator.server.dao;

import administrator.exception.DataIntegrityViolationException;
import administrator.server.model.RobotEntity;
import administrator.server.storage.RobotStorage;
import common.utils.Greenfield;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class RobotDao {
    private final RobotStorage storageRef;

    public RobotDao() {
        storageRef = RobotStorage.getInstance();
    }

    public void acquireLock() {
        synchronized (storageRef.getLock()) {
            while (storageRef.getLockAcquired()) {
                try {
                    storageRef.getLock().wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            storageRef.setLockAcquired(true);
        }
    }

    public void releaseLock() {
        synchronized (storageRef.getLock()) {
            storageRef.setLockAcquired(false);
            storageRef.getLock().notifyAll();
        }
    }

    public synchronized Optional<RobotEntity> getRobotById(String robotId) {
        for (RobotEntity robotEntity : storageRef.getRobotData()) {
            if (robotEntity.getId().equals(robotId)) {
                return Optional.of(robotEntity);
            }
        }

        return Optional.empty();
    }

    public synchronized List<RobotEntity> getAllRobots() {
        return new LinkedList<>(storageRef.getRobotData());
    }

    public synchronized void addRobot(RobotEntity robot) throws DataIntegrityViolationException {
        for (RobotEntity robotEntity : storageRef.getRobotData()) {
            if (robotEntity.getId().equals(robot.getId())) {
                throw new DataIntegrityViolationException("Robot with id " + robot.getId() + " already exists");
            }
        }

        storageRef.getRobotData().add(robot);
    }


    public synchronized void removeRobotById(String robotId) throws DataIntegrityViolationException {
        boolean removed = storageRef.getRobotData().removeIf(robotEntity -> robotEntity.getId().equals(robotId));

        if (!removed) {
            throw new DataIntegrityViolationException("Robot with id " + robotId + " does not exist");
        }
    }

    public synchronized int getLessPopulatedDistrict() {
        int[] districts = new int[4];
        for (RobotEntity robotEntity : storageRef.getRobotData()) {
            int districtIdx = Greenfield.getDistrictFromPosition(robotEntity.getPosition()) - 1;
            districts[districtIdx]++;
        }

        int min = Integer.MAX_VALUE;
        int minIndex = 0;
        for (int i = 0; i < districts.length; i++) {
            if (districts[i] < min) {
                min = districts[i];
                minIndex = i;
            }
        }

        return minIndex + 1;
    }
}