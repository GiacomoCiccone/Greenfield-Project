package common.response;

import common.bean.RobotInfoBean;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class RobotInitializationResponse {

    int x;

    int y;

    List<RobotInfoBean> otherRobots;

    public RobotInitializationResponse() {
    }

    public RobotInitializationResponse(int x, int y, List<RobotInfoBean> otherRobots) {
        this.x = x;
        this.y = y;
        this.otherRobots = otherRobots;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public List<RobotInfoBean> getOtherRobots() {
        return otherRobots;
    }

    public void setOtherRobots(List<RobotInfoBean> otherRobots) {
        this.otherRobots = otherRobots;
    }


}
