package administrator.server.robot;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/robotMock")
public class RobotAPI {

    @GET
    public String get() {
        return "Hello World!";
    }

}
