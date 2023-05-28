package robot.adapter;

import common.bean.RobotInfoBean;
import common.utils.Position;
import robot.model.RobotInfo;

public class RobotInfoAdapter {
    public static RobotInfo adapt(RobotInfoBean robotInfoBean) {
        String id = robotInfoBean.getId();
        int port = robotInfoBean.getPort();
        String ip = robotInfoBean.getAddress();
        Position position = new Position(robotInfoBean.getX(), robotInfoBean.getY());

        return new RobotInfo(id, port, ip, position);
    }
}