package robot.task;

import com.google.gson.Gson;
import common.json.MeasurementJsonSchema;
import common.json.RobotPollutionDataJsonSchema;
import org.eclipse.paho.client.mqttv3.MqttException;
import robot.adapter.MeasurementAdapter;
import robot.communication.MQTTPublisher;
import robot.model.PollutionDataStorage;
import robot.model.RobotInfo;
import robot.simulator.Measurement;
import utils.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class SensorDataPublisher extends RobotTaskBase {
    private static final String TOPIC_BASE_ADDRESS = "greenfield/pollution/district";
    public static final int PUBLISH_INTERVAL_MILLISECONDS = 15000;
    RobotInfo robotInfo;
    PollutionDataStorage storage;
    MQTTPublisher publisher;

    public SensorDataPublisher(RobotInfo robotInfo, PollutionDataStorage storage) {
        this.robotInfo = robotInfo;
        this.storage = storage;
        try {
            publisher = new MQTTPublisher();
        } catch (MqttException e) {
            // Send stop signal to the robot
            Logger.logException(e);
        }
    }

    @Override
    public void run() {
        Logger.debug("MQTT publisher started");
        try {
            publisher.connect();
            Logger.debug("MQTT publisher connected");
        } catch (MqttException e) {
            // Send stop signal to the robot
            Logger.logException(e);
        }
        while (isRunning()) {

            // Wait interval
            try {
                Thread.sleep(PUBLISH_INTERVAL_MILLISECONDS);
            } catch (InterruptedException e) {
                Logger.logException(e);
                break;
            }

            // Get data from storage and check if there is any
            List<Measurement> measurements = storage.getAllMeasurementsAndClear();
            if (measurements == null || measurements.isEmpty()) {
                continue;
            }

            // Publish data to MQTT
            List<MeasurementJsonSchema> jsonSchemaList = measurements.stream()
                    .map(MeasurementAdapter::adapt)
                    .collect(Collectors.toList());

            RobotPollutionDataJsonSchema robotPollutionDataJsonSchema = new RobotPollutionDataJsonSchema(robotInfo.getId(), jsonSchemaList, System.currentTimeMillis());

            String message = new Gson().toJson(robotPollutionDataJsonSchema);

            try {
                publisher.publish(TOPIC_BASE_ADDRESS + robotInfo.getDistrict(), message);
            } catch (MqttException e) {
                // Not necessary to stop the robot
                Logger.logException(e);
            }

        }

        try {
            publisher.disconnect();
            Logger.debug("MQTT publisher disconnected");
        } catch (MqttException e) {
            // Send stop signal to the robot
            Logger.logException(e);
        }
        Logger.debug("MQTT publisher stopped");
    }
}
