package administrator.server.adapter;

import administrator.server.model.RobotEntity;
import common.bean.RobotInfoBean;
import common.utils.Position;

public class RobotEntityAdapter {

    public static RobotInfoBean adapt(RobotEntity robotEntity) {
        Position position = robotEntity.getPosition();
        int xPosition = position.getX();
        int yPosition = position.getY();
        String id = robotEntity.getId();
        int port = robotEntity.getPort();
        String address = robotEntity.getAddress();

        return new RobotInfoBean(xPosition, yPosition, id, port, address);
    }

    public static RobotEntity adapt(RobotInfoBean robotInfoBean) {
        int xPosition = robotInfoBean.getxPosition();
        int yPosition = robotInfoBean.getyPosition();
        String id = robotInfoBean.getId();
        int port = robotInfoBean.getPort();
        String address = robotInfoBean.getAddress();
        Position position = new Position(xPosition, yPosition);

        return new RobotEntity(id, address, port, position);
    }
}
