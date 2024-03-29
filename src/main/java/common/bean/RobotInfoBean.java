package common.bean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RobotInfoBean {

    private int x;
    private int y;
    private String id;
    private int port;
    private String address;

    public RobotInfoBean() {
    }

    public RobotInfoBean(int x, int y, String id, int port, String address) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.port = port;
        this.address = address;
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
