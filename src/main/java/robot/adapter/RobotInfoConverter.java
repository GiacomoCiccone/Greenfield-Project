package robot.adapter;

import common.bean.RobotInfoBean;
import common.utils.Position;
import robot.RobotServiceOuterClass;
import robot.network.RobotInfo;

public class RobotInfoConverter {
    public static RobotInfo convert(RobotInfoBean robotInfoBean) {
        String id = robotInfoBean.getId();
        int port = robotInfoBean.getPort();
        String ip = robotInfoBean.getAddress();
        Position position = new Position(robotInfoBean.getX(), robotInfoBean.getY());

        return new RobotInfo(id, port, ip, position);
    }

    public static RobotInfo convert(RobotServiceOuterClass.RobotInfo robotInfo) {
        String id = robotInfo.getId();
        int port = robotInfo.getPort();
        String address = robotInfo.getAddress();
        int x = robotInfo.getX();
        int y = robotInfo.getY();
        Position position = new Position(x, y);

        return new RobotInfo(id, port, address, position);
    }

    public static RobotServiceOuterClass.RobotInfo convert(RobotInfo robotInfo) {
        RobotServiceOuterClass.RobotInfo.Builder builder = RobotServiceOuterClass.RobotInfo.newBuilder();

        builder.setId(robotInfo.getId());
        builder.setPort(robotInfo.getPort());
        builder.setAddress(robotInfo.getAddress());
        builder.setX(robotInfo.getPosition().getX());
        builder.setY(robotInfo.getPosition().getY());

        return builder.build();
    }

}