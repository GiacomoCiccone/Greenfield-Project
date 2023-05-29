package administrator.server.infrastructure.rest;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import utils.Logger;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;

public class RESTServer {

    private static final String BASE_URI = "http://localhost:8080/";
    private final HttpServer server;
    private boolean started;

    static {
        java.util.logging.Logger COM_SUN_JERSEY_LOGGER = java.util.logging.Logger.getLogger("com.sun.jersey");
        COM_SUN_JERSEY_LOGGER.setLevel(Level.SEVERE);
    }

    public RESTServer() {
        try {
            URI uri = UriBuilder.fromUri(BASE_URI).build();
            server = HttpServerFactory.create(uri);
            started = false;

            Logger.info("REST server initialized with base URI: " + BASE_URI);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize REST Server", e);
        }
    }


    public void start() {
        server.start();
        started = true;

        Logger.info("REST server started");
    }

    public void stop() {
        if (started) {
            server.stop(0);
            started = false;

            Logger.info("REST server stopped");
        }
    }


}
