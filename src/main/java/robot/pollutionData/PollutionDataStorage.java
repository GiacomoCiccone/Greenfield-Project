package robot.pollutionData;

import robot.simulator.Measurement;

import java.util.ArrayList;
import java.util.List;

public class PollutionDataStorage implements PollutionDataRepository {

    private final List<Measurement> measurements;
    private int lastMeasurementIndex;

    public PollutionDataStorage() {
        this.measurements = new ArrayList<>();
        this.lastMeasurementIndex = 0;
    }

    @Override
    public synchronized void addWindow(List<Measurement> window) {
        if (window == null) {
            return;
        }
        measurements.addAll(window);
    }

    @Override
    public List<Measurement> getAllMeasurementsFromLastRead() {
        List<Measurement> result = new ArrayList<>();
        for (int i = lastMeasurementIndex; i < measurements.size(); i++) {
            Measurement measurement = measurements.get(i);
            Measurement copy = new Measurement(measurement.getId(), measurement.getType(), measurement.getValue(), measurement.getTimestamp());
            result.add(copy);
        }
        lastMeasurementIndex = measurements.size();
        return result;
    }

    @Override
    public void clear() {
        measurements.clear();
        lastMeasurementIndex = 0;
    }

}
