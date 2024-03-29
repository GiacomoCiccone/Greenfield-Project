package robot.task;

import com.google.gson.Gson;
import common.json.RobotPollutionDataJsonSchema;
import org.eclipse.paho.client.mqttv3.MqttException;
import robot.adapter.MeasurementConverter;
import robot.communication.MQTTPublisher;
import robot.context.RobotContext;
import robot.context.RobotContextProvider;
import robot.pollutionData.PollutionDataRepository;
import robot.pollutionData.PollutionDataRepositoryProvider;
import robot.simulator.Measurement;
import common.utils.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class SensorDataPublisher extends RobotTaskBase {
    private static final String TOPIC_BASE_ADDRESS = "greenfield/pollution/district";
    public static final int PUBLISH_INTERVAL_MILLISECONDS = 15000;
    private final PollutionDataRepository rawTable;
    private final RobotContext context;
    private final MQTTPublisher publisher;


    public SensorDataPublisher() {
        this.rawTable = PollutionDataRepositoryProvider.getRepository();
        this.context = RobotContextProvider.getContext();
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
            Logger.info("MQTT publisher connected");
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
            List<Measurement> measurements = rawTable.getAllMeasurementsFromLastRead();
            if (measurements == null || measurements.isEmpty()) {
                continue;
            }

            // Publish data to MQTT
            List<RobotPollutionDataJsonSchema.MeasurementJsonSchema> jsonSchemaList = measurements.stream()
                    .map(MeasurementConverter::convert)
                    .collect(Collectors.toList());

            RobotPollutionDataJsonSchema schema = new RobotPollutionDataJsonSchema(context.getId(), jsonSchemaList, System.currentTimeMillis());
            String message = new Gson().toJson(schema);

            try {
                publisher.publish(TOPIC_BASE_ADDRESS + context.getDistrict(), message);
            } catch (MqttException e) {
                Logger.warning("MQTT publisher could not publish message");
            }

        }

        try {
            publisher.disconnect();
            Logger.info("MQTT publisher disconnected");
        } catch (MqttException e) {
            Logger.warning("MQTT publisher could not disconnect");
        }
        Logger.info("MQTT publisher stopped");
    }
}
