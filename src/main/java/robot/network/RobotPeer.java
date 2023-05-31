package robot.network;

import common.utils.Greenfield;
import common.utils.Position;

public class RobotPeer {
    String id;
    int port;
    String address;
    Position position;

    public RobotPeer(String id, int port, String address, Position position) {
        this.id = id;
        this.port = port;
        this.address = address;
        this.position = position;
    }

    public RobotPeer() {
        this.id = "";
        this.port = 0;
        this.address = "";
        this.position = new Position(0, 0);
    }

    public RobotPeer(RobotPeer other) {
        this.id = other.getId();
        this.port = other.getPort();
        this.address = other.getAddress();
        this.position = new Position(other.position);
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
        return new Position(position.getX(), position.getY());
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

    public int getDistrict() {
        return Greenfield.getDistrictFromPosition(position);
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
