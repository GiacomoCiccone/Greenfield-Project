package administrator.server.infrastructure.mqtt;


import administrator.server.adapter.PollutionDataEntityAdapter;
import administrator.server.dao.PollutionDao;
import administrator.server.model.PollutionDataEntity;
import com.google.gson.Gson;
import common.json.RobotPollutionDataJsonSchema;
import org.eclipse.paho.client.mqttv3.*;
import common.utils.Logger;

public class MQTTSubscriberCallback implements MqttCallback {

    @Override
    public void connectionLost(Throwable cause) {
        throw new RuntimeException("Connection to MQTT broker lost", cause);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        Logger.info("Message arrived from topic " + topic);

        String payload = new String(message.getPayload());

        Gson gson = new Gson();
        RobotPollutionDataJsonSchema robotPollutionDataJsonSchema = gson.fromJson(payload, RobotPollutionDataJsonSchema.class);

        PollutionDataEntity pollutionDataEntity = PollutionDataEntityAdapter.adapt(robotPollutionDataJsonSchema);

        PollutionDao pollutionDao = new PollutionDao();
        pollutionDao.addPollutionDataEntry(pollutionDataEntity);

        Logger.info("Added pollution data entry with " + pollutionDataEntity.getPollutionData().size() + " measurements");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Logger.info("Delivery complete: " + token);
    }
}

