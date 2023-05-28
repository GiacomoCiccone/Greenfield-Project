package administrator.server.storage;

import administrator.server.model.PollutionDataEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollutionStorage {

    Map<String, List<PollutionDataEntity>> pollutionData = new HashMap<>();

    private static final PollutionStorage instance = new PollutionStorage();

    private PollutionStorage() {
    }

    public static synchronized PollutionStorage getInstance() {
        return instance;
    }

    public Map<String, List<PollutionDataEntity>> getPollutionData() {
        return pollutionData;
    }

}
