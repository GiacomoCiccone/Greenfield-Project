package administrator.server.robot;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/robot")
public class RobotAPI {

    @GET
    public String get() {
        return "Hello World!";
    }

}
