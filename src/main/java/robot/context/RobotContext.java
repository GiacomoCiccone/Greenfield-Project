package robot.context;

import common.utils.Greenfield;
import common.utils.Position;
import robot.Robot;
import robot.network.RobotPeer;

public class RobotContext {
    private final String id;
    private final String address;
    private final int port;
    private Position position;

    RobotContext(String id, String address, int port, Position position) {
        this.id = id;
        this.address = address;
        this.port = port;
        this.position = position;
    }

    public synchronized String getId() {
        return id;
    }

    public synchronized String getAddress() {
        return address;
    }

    public synchronized int getPort() {
        return port;
    }

    public synchronized Position getPosition() {
        return position;
    }

    public synchronized int getDistrict() {
        return Greenfield.getDistrictFromPosition(position);
    }

    public synchronized RobotPeer getRobotInfo() {
        return new RobotPeer(id, port, address, position);
    }

}
