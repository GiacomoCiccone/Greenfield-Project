package administrator.server;

import administrator.server.infrastructure.JerseyServer;
import administrator.server.infrastructure.MQTTClient;
import utils.Logger;

import java.util.Scanner;

public class Server {

    private static final JerseyServer jerseyServer = new JerseyServer();
//    private static final MQTTClient mqttClient = new MQTTClient();

    public static void main(String[] args) {


        jerseyServer.start();
//        mqttClient.connect();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter 'q' to quit: ");
            try {
                String c = scanner.nextLine().toLowerCase();
                if (c.equals("q")) {
                    break;
                }
            } catch (Exception e) {
                Logger.logException(e);
            }
        }

        jerseyServer.stop();
//        mqttClient.disconnect();
    }
}
