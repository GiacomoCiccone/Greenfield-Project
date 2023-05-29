package administrator.server.model;

import common.utils.Position;

public class RobotEntity {
    private String id;
    private String address;
    private int port;
    Position position;

    public RobotEntity(String id, String address, int port, Position position) {
        this.id = id;
        this.address = address;
        this.port = port;
        this.position = position;
    }

    public RobotEntity(RobotEntity other) {
        this.id = other.id;
        this.address = other.address;
        this.port = other.port;
        this.position = new Position(other.position);
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public Position getPosition() {
        return position;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPosition(Position position) {
        this.position = position;
    }


}
