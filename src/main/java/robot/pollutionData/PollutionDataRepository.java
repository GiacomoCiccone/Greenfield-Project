package robot.pollutionData;

import robot.simulator.Measurement;

import java.util.List;

public interface PollutionDataRepository {
    void addWindow(List<Measurement> window);

    List<Measurement> getAllMeasurementsFromLastRead();

    void clear();
}

