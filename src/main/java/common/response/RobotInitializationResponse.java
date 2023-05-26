package common.response;

import common.bean.RobotInfoBean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class RobotInitializationResponse {

    @XmlElement
    int xPosition;

    @XmlElement
    int yPosition;

    @XmlElement
    List<RobotInfoBean> otherRobots;

    public RobotInitializationResponse() {
    }

    public RobotInitializationResponse(int xPosition, int yPosition, List<RobotInfoBean> otherRobots) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.otherRobots = otherRobots;
    }

    public int getxPosition() {
        return xPosition;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public List<RobotInfoBean> getOtherRobots() {
        return otherRobots;
    }

    public void setOtherRobots(List<RobotInfoBean> otherRobots) {
        this.otherRobots = otherRobots;
    }


}
