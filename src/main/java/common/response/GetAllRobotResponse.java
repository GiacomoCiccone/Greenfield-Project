package common.response;

import common.bean.RobotInfoBean;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class GetAllRobotResponse {
    List<RobotInfoBean> list;

    public GetAllRobotResponse() {
    }

    public GetAllRobotResponse(List<RobotInfoBean> list) {
        this.list = list;
    }

    public List<RobotInfoBean> getList() {
        return list;
    }

    public void setList(List<RobotInfoBean> list) {
        this.list = list;
    }
}
