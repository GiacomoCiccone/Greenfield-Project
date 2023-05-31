package robot.task;

import robot.pollutionData.PollutionDataRepository;
import robot.simulator.PM10Simulator;
import robot.simulator.PollutionBuffer;
import utils.Logger;

public class SensorDataReader extends RobotTaskBase {
    private final PollutionBuffer pollutionBuffer;
    private final PM10Simulator pm10Simulator;
    private final PollutionDataRepository rawTable;

    public SensorDataReader(PollutionDataRepository rawTable) {
        this.pollutionBuffer = new PollutionBuffer();
        this.pm10Simulator = new PM10Simulator(pollutionBuffer);
        this.rawTable = rawTable;
    }

    @Override
    public void run() {
        Logger.info("Sensor data reader started");

        pm10Simulator.start();
        while (isRunning()) {
            rawTable.addWindow(pollutionBuffer.readAllAndClean());
        }
        pm10Simulator.stopMeGently();

        Logger.info("Sensor data reader stopped");
    }
}
