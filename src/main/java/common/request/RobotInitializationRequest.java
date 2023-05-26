package common.request;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RobotInitializationRequest {

    @XmlElement
    String id;

    @XmlElement
    String address;

    @XmlElement
    int port;

    public RobotInitializationRequest(String id, String address, int port) {
        this.id = id;
        this.address = address;
        this.port = port;
    }

    public RobotInitializationRequest() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
