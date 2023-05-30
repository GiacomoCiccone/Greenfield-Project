package robot.task;

import com.google.gson.Gson;
import common.json.RobotPollutionDataJsonSchema;
import org.eclipse.paho.client.mqttv3.MqttException;
import robot.adapter.MeasurementAdapter;
import robot.communication.MQTTPublisher;
import robot.core.RobotContextProvider;
import robot.model.PollutionDataStorage;
import robot.simulator.Measurement;
import utils.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class SensorDataPublisher extends RobotTaskBase {
    private static final String TOPIC_BASE_ADDRESS = "greenfield/pollution/district";
    public static final int PUBLISH_INTERVAL_MILLISECONDS = 15000;
    PollutionDataStorage storage;
    MQTTPublisher publisher;

    public SensorDataPublisher(PollutionDataStorage storage) {
        this.storage = storage;
        try {
            publisher = new MQTTPublisher();
        } catch (MqttException e) {
            throw new RuntimeException("MQTT publisher could not be created", e);
        }
    }

    @Override
    public void run() {
        Logger.info("MQTT publisher started");
        try {
            publisher.connect();
            Logger.debug("MQTT publisher connected");
        } catch (MqttException e) {
            throw new RuntimeException("MQTT publisher could not connect", e);
        }
        while (isRunning()) {

            // Wait interval
            try {
                Thread.sleep(PUBLISH_INTERVAL_MILLISECONDS);
            } catch (InterruptedException e) {
                break;
            }

            // Get data from storage and check if there is any
            List<Measurement> measurements = storage.getAllMeasurementsAndClear();
            if (measurements == null || measurements.isEmpty()) {
                continue;
            }

            // Publish data to MQTT
            List<RobotPollutionDataJsonSchema.MeasurementJsonSchema> jsonSchemaList = measurements.stream()
                    .map(MeasurementAdapter::adapt)
                    .collect(Collectors.toList());

            RobotPollutionDataJsonSchema robotPollutionDataJsonSchema = new RobotPollutionDataJsonSchema(RobotContextProvider.getCurrentRobot().getId(), jsonSchemaList, System.currentTimeMillis());

            String message = new Gson().toJson(robotPollutionDataJsonSchema);

            try {
                publisher.publish(TOPIC_BASE_ADDRESS + RobotContextProvider.getCurrentRobot().getDistrict(), message);
            } catch (MqttException e) {
                Logger.warning("MQTT publisher could not publish message");
            }

        }

        try {
            publisher.disconnect();
            Logger.debug("MQTT publisher disconnected");
        } catch (MqttException e) {
            Logger.warning("MQTT publisher could not disconnect");
        }
        Logger.debug("MQTT publisher stopped");
    }
}
