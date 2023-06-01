package administrator.server;

import administrator.server.infrastructure.rest.RESTServer;
import administrator.server.infrastructure.mqtt.MQTTClient;
import common.utils.Logger;

import java.util.Scanner;

public class Server {

    private final RESTServer restServer;
    private final MQTTClient mqttClient;

    public Server() {
        restServer = new RESTServer();
        mqttClient = new MQTTClient();
    }

    public static void main(String[] args) {
        Logger.info("Starting server...");

        try {
            Server server = new Server();
            server.restServer.start();
            server.mqttClient.connectAndSubscribe();

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Enter 'q' to quit: ");
                try {
                    String c = scanner.nextLine().toLowerCase();
                    if (c.equals("q")) {
                        break;
                    }
                } catch (Exception e) {
                    Logger.warning("Failed to read user input");
                }
            }

            server.restServer.stop();
            server.mqttClient.disconnectAndClose();

            Logger.info("Server stopped");
        } catch (Exception e) {
            Logger.error("Server stopped with error: " + e.getMessage());
            Logger.logException(e);
        }
    }
}
