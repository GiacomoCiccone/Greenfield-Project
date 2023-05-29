package administrator.server.infrastructure.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import utils.Logger;

import java.util.Arrays;

public class MQTTClient {

    private final MqttClient clientInstance;
    private boolean connected = false;

    public MQTTClient() {
        try {
            this.clientInstance = new MqttClient(MQTTConfig.BROKER_URL, MqttClient.generateClientId());
        } catch (MqttException e) {
            throw new RuntimeException("Failed to initialize MQTTClient", e);
        }
    }

    public void connectAndSubscribe() {
        try {
            clientInstance.setCallback(new MQTTSubscriberCallback());

            clientInstance.connect();
            connected = true;
            Logger.info("Connected to MQTT broker: " + MQTTConfig.BROKER_URL);

            clientInstance.subscribe(MQTTConfig.TOPICS);
            Logger.info("Subscribed to topic: " + Arrays.toString(MQTTConfig.TOPICS));
        } catch (MqttException e) {
            throw new RuntimeException("Failed to connect to MQTT broker", e);
        }
    }

    public void disconnectAndClose() {
        if (!connected) {
            return;
        }

        try {
            clientInstance.disconnect();
            clientInstance.close();
            connected = false;

            Logger.info("Disconnected from MQTT broker: " + MQTTConfig.BROKER_URL);
        } catch (MqttException e) {
            throw new RuntimeException("Failed to disconnect from MQTT broker", e);
        }
    }
}
