package administrator.server.dao;

import administrator.server.exception.NotFoundException;
import administrator.server.model.PollutionDataEntity;
import administrator.server.storage.PollutionStorage;
import common.utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class PollutionDao {

    private final PollutionStorage storageRef;

    public PollutionDao() {
        storageRef = PollutionStorage.getInstance();
    }

    public synchronized void addPollutionDataEntry(PollutionDataEntity pollutionDataEntity) {
        String robotId = pollutionDataEntity.getRobotId();

        // If there is no pollution data for this robot, create a new list
        if (!storageRef.getPollutionData().containsKey(robotId)) {
            storageRef.getPollutionData().put(robotId, new ArrayList<PollutionDataEntity>());
        }

        Logger.info("Adding pollution data entry for robot with id " + robotId);
        storageRef.getPollutionData().get(robotId).add(pollutionDataEntity);

        RobotDao robotDao = new RobotDao();
    }

    public synchronized List<PollutionDataEntity> getLastNPollutionEntriesByRobotId(int n, String robotId) throws NotFoundException {
        if (!storageRef.getPollutionData().containsKey(robotId)) {
            throw new NotFoundException("Pollution data for robot with id " + robotId + " does not exist");
        }

        List<PollutionDataEntity> pollutionData = storageRef.getPollutionData().get(robotId);

        if (pollutionData.size() < n) {
            throw new NotFoundException("Robot with id " + robotId + " does not have enough pollution data entries");
        }

        int size = pollutionData.size();
        int startIndex = size - n;

        // return deep copy of the sublist
        List<PollutionDataEntity> result = new ArrayList<>(n);
        for (int i = startIndex; i < size; i++) {
            result.add(new PollutionDataEntity(pollutionData.get(i)));
        }

        return result;
    }

    public synchronized List<PollutionDataEntity> getAllPollutionEntriesInInterval(long t1, long t2) {
        List<PollutionDataEntity> result = new ArrayList<>();

        for (List<PollutionDataEntity> pollutionDataEntries : storageRef.getPollutionData().values()) {
            for (PollutionDataEntity pollutionDataEntity : pollutionDataEntries) {
                if (pollutionDataEntity.getTimestamp() >= t1 && pollutionDataEntity.getTimestamp() <= t2) {
                    result.add(new PollutionDataEntity(pollutionDataEntity));
                }
            }
        }

        return result;
    }
}
