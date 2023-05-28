package administrator.server.adapter;

import administrator.server.model.RobotEntity;
import common.bean.RobotInfoBean;
import common.utils.Position;

public class RobotEntityAdapter {

    public static RobotInfoBean adapt(RobotEntity robotEntity) {
        Position position = robotEntity.getPosition();
        int x = position.getX();
        int y = position.getY();
        String id = robotEntity.getId();
        int port = robotEntity.getPort();
        String address = robotEntity.getAddress();

        return new RobotInfoBean(x, y, id, port, address);
    }
}
