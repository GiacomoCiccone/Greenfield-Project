package administrator.server.dao;

import administrator.server.exception.DataIntegrityViolationException;
import administrator.server.exception.NotFoundException;
import administrator.server.model.RobotEntity;
import administrator.server.storage.RobotStorage;
import common.utils.Greenfield;
import common.utils.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RobotDao {
    public static final int SINGLE_ROBOT_TIMEOUT = 60000;
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
                return Optional.of(new RobotEntity(robotEntity));
            }
        }

        return Optional.empty();
    }

    public synchronized List<RobotEntity> getAllRobots() {
        return storageRef.getRobotData().stream()
                .map(RobotEntity::new)
                .collect(Collectors.toList());
    }

    public synchronized void addRobot(RobotEntity robot) throws DataIntegrityViolationException {
        for (RobotEntity robotEntity : storageRef.getRobotData()) {
            if (robotEntity.getId().equals(robot.getId())) {
                throw new DataIntegrityViolationException("Robot with id " + robot.getId() + " already exists");
            }
        }

        storageRef.getRobotData().add(robot);

        Logger.info("Added robot with id " + robot.getId() + " to storage");

        if (storageRef.getRobotData().size() == 1) {
            Logger.info("RobotStorage thread interrupted");
            storageRef.setRunnable(() -> {
                try {
                    Thread.sleep(SINGLE_ROBOT_TIMEOUT);
                } catch (InterruptedException e) {
                    Logger.warning("RobotStorage thread interrupted");
                    return;
                }

                try {
                    removeRobotById(robot.getId());
                } catch (NotFoundException e) {
                    Logger.warning(e.getMessage());
                }
            });
            storageRef.startRunnable();
            Logger.info("Started thread on RobotStorage");
        } else {
            storageRef.stopRunnable();
            Logger.info("Stopped thread on RobotStorage");
        }
    }


    public synchronized void removeRobotById(String robotId) throws NotFoundException {
        boolean removed = storageRef.getRobotData().removeIf(robotEntity -> robotEntity.getId().equals(robotId));

        if (!removed) {
            throw new NotFoundException("Robot with id " + robotId + " does not exist");
        }

        Logger.info("Removed robot with id " + robotId + " from storage");

        if (storageRef.getRobotData().size() == 1) {
            storageRef.setRunnable(() -> {
                Logger.info("RobotStorage thread interrupted");
                try {
                    Thread.sleep(SINGLE_ROBOT_TIMEOUT);
                } catch (InterruptedException e) {
                    Logger.warning("RobotStorage thread interrupted");
                    return;
                }

                try {
                    removeRobotById(robotId);
                } catch (NotFoundException e) {
                    Logger.warning(e.getMessage());
                }
            });
            storageRef.startRunnable();
            Logger.info("Started thread on RobotStorage");
        } else {
            storageRef.stopRunnable();
            Logger.info("Stopped thread on RobotStorage");
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

    synchronized void resetRunnable(String robotId) {
        if (storageRef.getRobotData().size() == 1) {
            storageRef.stopRunnable();
            storageRef.setRunnable(() -> {
                Logger.info("RobotStorage thread started");
                try {
                    Thread.sleep(SINGLE_ROBOT_TIMEOUT);
                } catch (InterruptedException e) {
                    Logger.warning("RobotStorage thread interrupted");
                    return;
                }

                try {
                    removeRobotById(robotId);
                } catch (NotFoundException e) {
                    Logger.warning(e.getMessage());
                }
            });
            storageRef.startRunnable();
        } else {
            storageRef.stopRunnable();
        }
    }
}
