package robot.adapter;

import common.bean.RobotInfoBean;
import common.utils.Position;
import robot.RobotServiceOuterClass;
import robot.network.RobotPeer;

public class RobotPeerAdapter {
    public static RobotPeer adapt(RobotInfoBean robotInfoBean) {
        String id = robotInfoBean.getId();
        int port = robotInfoBean.getPort();
        String ip = robotInfoBean.getAddress();
        Position position = new Position(robotInfoBean.getX(), robotInfoBean.getY());

        return new RobotPeer(id, port, ip, position);
    }

    public static RobotPeer adapt(RobotServiceOuterClass.RobotInfo robotInfo) {
        String id = robotInfo.getId();
        int port = robotInfo.getPort();
        String address = robotInfo.getAddress();
        int x = robotInfo.getX();
        int y = robotInfo.getY();
        Position position = new Position(x, y);

        return new RobotPeer(id, port, address, position);
    }

    public static RobotServiceOuterClass.RobotInfo adapt(RobotPeer robotPeer) {
        RobotServiceOuterClass.RobotInfo.Builder builder = RobotServiceOuterClass.RobotInfo.newBuilder();

        builder.setId(robotPeer.getId());
        builder.setPort(robotPeer.getPort());
        builder.setAddress(robotPeer.getAddress());
        builder.setX(robotPeer.getPosition().getX());
        builder.setY(robotPeer.getPosition().getY());

        return builder.build();
    }

}