package robot.adapter;

import common.bean.RobotInfoBean;
import common.utils.Position;
import robot.RobotServiceOuterClass;
import robot.model.RobotInfo;

public class RobotInfoAdapter {
    public static RobotInfo adapt(RobotInfoBean robotInfoBean) {
        String id = robotInfoBean.getId();
        int port = robotInfoBean.getPort();
        String ip = robotInfoBean.getAddress();
        Position position = new Position(robotInfoBean.getX(), robotInfoBean.getY());

        return new RobotInfo(id, port, ip, position);
    }

    public static RobotInfo adapt(RobotServiceOuterClass.RobotInfo robotInfo) {
        String id = robotInfo.getId();
        int port = robotInfo.getPort();
        String address = robotInfo.getAddress();
        int x = robotInfo.getX();
        int y = robotInfo.getY();
        Position position = new Position(x, y);

        return new RobotInfo(id, port, address, position);
    }

    public static RobotServiceOuterClass.RobotInfo adapt(RobotInfo robotInfo) {
        RobotServiceOuterClass.RobotInfo.Builder builder = RobotServiceOuterClass.RobotInfo.newBuilder();

        builder.setId(robotInfo.getId());
        builder.setPort(robotInfo.getPort());
        builder.setAddress(robotInfo.getAddress());
        builder.setX(robotInfo.getPosition().getX());
        builder.setY(robotInfo.getPosition().getY());

        return builder.build();
    }

}