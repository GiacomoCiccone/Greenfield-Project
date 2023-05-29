package robot.communication;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MQTTPublisher {

    public static final String BROKER_URL = "tcp://localhost:1883";
    public static final int QOS = 2;
    MqttClient mqttClient;

    public MQTTPublisher() throws MqttException {
        mqttClient = new MqttClient(BROKER_URL, MqttClient.generateClientId());
    }

    public void connect() throws MqttException {
        mqttClient.connect();
    }

    public void disconnect() throws MqttException {
        mqttClient.disconnect();
    }

    public void publish(String topic, String message) throws MqttException {
        mqttClient.publish(topic, message.getBytes(), QOS, false);
    }
}
