package administrator.server.infrastructure;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import utils.Logger;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;

public class JerseyServer {
    private final String baseUri;
    private HttpServer server;

    public JerseyServer(String baseUri) {
        this.baseUri = baseUri;
    }

    static {
        java.util.logging.Logger COM_SUN_JERSEY_LOGGER = java.util.logging.Logger.getLogger("com.sun.jersey");
        COM_SUN_JERSEY_LOGGER.setLevel(Level.SEVERE);
    }

    public void initialize() {
        try {
            URI uri = UriBuilder.fromUri(baseUri).build();
            server = HttpServerFactory.create(uri);
            Logger.info("JerseyServer initialized with base URI: " + baseUri);
        } catch (IOException e) {
            Logger.logException(e);
            throw new RuntimeException("Failed to initialize JerseyServer", e);
        }
    }

    public void start() {
        if (server == null) {
            throw new IllegalStateException("JerseyServer not initialized");
        }

        server.start();
        Logger.info("JerseyServer started");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            Logger.info("JerseyServer stopped");
        }
    }


}
