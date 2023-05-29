package robot.task;

import robot.model.PollutionDataStorage;
import robot.simulator.PM10Simulator;
import robot.simulator.PollutionBuffer;
import utils.Logger;

public class SensorDataReader extends RobotTaskBase {
    private final PollutionBuffer pollutionBuffer;
    private final PM10Simulator pm10Simulator;
    private final PollutionDataStorage pollutionDataStorage;

    public SensorDataReader(PollutionDataStorage pollutionDataStorage) {
        this.pollutionBuffer = new PollutionBuffer();
        this.pm10Simulator = new PM10Simulator(pollutionBuffer);
        this.pollutionDataStorage = pollutionDataStorage;
    }

    @Override
    public void run() {
        Logger.info("Sensor data reader started");

        pm10Simulator.start();
        while (isRunning()) {
            pollutionDataStorage.addMeasurements(pollutionBuffer.readAllAndClean());
        }
        pm10Simulator.stopMeGently();

        Logger.info("Sensor data reader stopped");
    }
}
