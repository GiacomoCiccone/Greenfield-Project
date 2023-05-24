package administrator.server.infrastructure;

import org.eclipse.paho.client.mqttv3.*;
import utils.Logger;

import java.util.Arrays;

public class MQTTClient {

    public static final String BROKER_URL = "tcp://localhost:1883";
    public static final int QOS = 2;
    public static final String[] TOPICS = {"greenfield/pollution/district1", "greenfield/pollution/district2",
            "greenfield/pollution/district3", "greenfield/pollution/district4"};
    private final MqttClient client;

    public MQTTClient() {
        try {
            this.client = new MqttClient(BROKER_URL, MqttClient.generateClientId());
        } catch (MqttException e) {
            Logger.logException(e);
            throw new RuntimeException("Failed to initialize MQTTClient", e);
        }
    }

    public void connect() {
        try {
            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    Logger.logException((Exception) cause);
                    throw new RuntimeException("Connection to MQTT broker lost", cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Logger.info("Message arrived: " + topic + " - " + message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Logger.info("Delivery complete: " + token);
                }
            });

            client.connect();
            client.subscribe(TOPICS);
            Logger.info("Connected to MQTT broker: " + BROKER_URL);
            Logger.info("Subscribed to topic: " + Arrays.toString(TOPICS));
        } catch (MqttException e) {
            Logger.logException(e);
            throw new RuntimeException("Failed to connect to MQTT broker", e);
        }
    }

    public void disconnect() {
        try {
            client.disconnect();
            client.close();
            Logger.info("Disconnected from MQTT broker: " + BROKER_URL);
        } catch (MqttException e) {
            Logger.logException(e);
            throw new RuntimeException("Failed to disconnect from MQTT broker", e);
        }
    }
}
