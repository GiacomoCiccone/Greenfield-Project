package robot.model;

import robot.simulator.Measurement;

import java.util.ArrayList;
import java.util.List;

public class PollutionDataStorage {

    private final List<Measurement> measurements;

    public PollutionDataStorage() {
        this.measurements = new ArrayList<>();
    }

    public synchronized void addMeasurements(List<Measurement> newMeasurements) {
        if (newMeasurements == null) {
            return;
        }
        measurements.addAll(newMeasurements);
    }

    public synchronized List<Measurement> getAllMeasurementsAndClear() {
        List<Measurement> allMeasurements = new ArrayList<>(measurements);
        measurements.clear();
        return allMeasurements;
    }

}
