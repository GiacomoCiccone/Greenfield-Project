package administrator.server.api;

import javax.ws.rs.core.HttpHeaders;

public class HostInfo {
    private final String address;
    private final int port;

    public HostInfo(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public static HostInfo parseHostInfo(HttpHeaders headers) {
        String hostHeader = headers.getRequestHeader("Host").get(0);
        String[] parts = hostHeader.split(":");
        String address = parts[0];
        int port = Integer.parseInt(parts[1]);

        return new HostInfo(address, port);
    }
}
