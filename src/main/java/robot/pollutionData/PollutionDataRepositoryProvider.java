package robot.pollutionData;

public class PollutionDataRepositoryProvider {
    private static PollutionDataRepository instance;

    private PollutionDataRepositoryProvider() {
    }

    public static synchronized PollutionDataRepository getRepository() {
        if (instance == null) {
            instance = new PollutionDataStorage();
        }
        return instance;
    }
}
