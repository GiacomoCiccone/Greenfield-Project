package administrator.server.infrastructure.mqtt;

public class MQTTConfig {
    public static final String BROKER_URL = "tcp://localhost:1883";
    public static final String[] TOPICS = {"greenfield/pollution/district1", "greenfield/pollution/district2",
            "greenfield/pollution/district3", "greenfield/pollution/district4"};
}
