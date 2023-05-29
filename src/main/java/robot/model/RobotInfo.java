package robot.model;

import common.utils.Greenfield;
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

    public RobotInfo() {
        this.id = "";
        this.port = 0;
        this.address = "";
        this.position = new Position(0, 0);
    }

    public synchronized String getId() {
        return id;
    }

    public synchronized int getPort() {
        return port;
    }

    public synchronized String getAddress() {
        return address;
    }

    public synchronized Position getPosition() {
        return new Position(position.getX(), position.getY());
    }

    public synchronized void setId(String id) {
        this.id = id;
    }

    public synchronized void setPort(int port) {
        this.port = port;
    }

    public synchronized void setAddress(String address) {
        this.address = address;
    }

    public synchronized void setPosition(Position position) {
        this.position = position;
    }

    public synchronized int getDistrict() {
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
