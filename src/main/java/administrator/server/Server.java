package administrator.server;

import administrator.server.infrastructure.JerseyServer;
import administrator.server.infrastructure.MQTTClient;

public class Server {

    private static final JerseyServer jerseyServer = new JerseyServer();
    private static final MQTTClient mqttClient = new MQTTClient();

    public static void main(String[] args) {


        jerseyServer.start();
        mqttClient.connect();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println();
        jerseyServer.stop();
        mqttClient.disconnect();
    }
}
