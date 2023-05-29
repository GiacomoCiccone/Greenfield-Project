package administrator.server.dao;

import administrator.exception.NotFoundException;
import administrator.server.model.PollutionDataEntity;
import administrator.server.storage.PollutionStorage;

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

        storageRef.getPollutionData().get(robotId).add(pollutionDataEntity);
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

        return pollutionData.subList(startIndex, size);
    }

    public synchronized List<PollutionDataEntity> getAllPollutionEntriesInInterval(long t1, long t2) {
        List<PollutionDataEntity> result = new ArrayList<>();

        for (List<PollutionDataEntity> pollutionDataEntries : storageRef.getPollutionData().values()) {
            for (PollutionDataEntity pollutionDataEntity : pollutionDataEntries) {
                if (pollutionDataEntity.getTimestamp() >= t1 && pollutionDataEntity.getTimestamp() <= t2) {
                    result.add(pollutionDataEntity);
                }
            }
        }


        return result;
    }
}
