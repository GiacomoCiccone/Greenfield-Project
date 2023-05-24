package administrator.server.infrastructure;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import utils.Logger;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;

public class JerseyServer {

    private static final String BASE_URI = "http://localhost:8080/";
    private final HttpServer server;

    static {
        java.util.logging.Logger COM_SUN_JERSEY_LOGGER = java.util.logging.Logger.getLogger("com.sun.jersey");
        COM_SUN_JERSEY_LOGGER.setLevel(Level.SEVERE);
    }

    public JerseyServer() {
        try {
            URI uri = UriBuilder.fromUri(BASE_URI).build();
            server = HttpServerFactory.create(uri);
            Logger.info("JerseyServer initialized with base URI: " + BASE_URI);
        } catch (IOException e) {
            Logger.logException(e);
            throw new RuntimeException("Failed to initialize JerseyServer", e);
        }
    }


    public void start() {
        server.start();
        Logger.info("JerseyServer started");
    }

    public void stop() {
        server.stop(0);
        Logger.info("JerseyServer stopped");
    }


}
