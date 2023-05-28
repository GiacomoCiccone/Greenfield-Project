package robot.model;

import common.utils.Position;

public class RobotInfo {
    String id;
    int port;
    String address;
    Position position;

    public RobotInfo(String id, int port, String address, Position position) {
        this.id = id;
        this.port = port;
        this.address = address;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public Position getPosition() {
        return position;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "RobotInfo{" +
                "id='" + id + '\'' +
                ", port=" + port +
                ", address='" + address + '\'' +
                ", position=" + position +
                '}';
    }
}
