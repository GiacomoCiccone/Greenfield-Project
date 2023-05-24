package administrator.server;

import administrator.server.infrastructure.JerseyServer;

import java.util.Scanner;

public class Server {

    private static final String BASE_URI = "http://localhost:8080/";

    private static final JerseyServer jerseyServer = new JerseyServer(BASE_URI);

    public static void main(String[] args) {
        jerseyServer.initialize();
        jerseyServer.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println();
        jerseyServer.stop();

    }
}
