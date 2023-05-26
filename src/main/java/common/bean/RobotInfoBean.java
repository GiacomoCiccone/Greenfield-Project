package common.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RobotInfoBean {

    @XmlElement
    private int xPosition;
    @XmlElement
    private int yPosition;
    @XmlElement
    private String id;
    @XmlElement
    private int port;
    @XmlElement
    private String address;

    public RobotInfoBean() {
    }

    public RobotInfoBean(int xPosition, int yPosition, String id, int port, String address) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.id = id;
        this.port = port;
        this.address = address;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
