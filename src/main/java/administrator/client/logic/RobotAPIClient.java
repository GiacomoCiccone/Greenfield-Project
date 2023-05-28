package administrator.client.logic;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import common.bean.RobotInfoBean;
import common.response.ErrorResponse;
import common.response.GetAllRobotResponse;
import utils.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class RobotAPIClient {
    private static final String API_URL = "http://localhost:8080";

    public List<RobotInfoBean> getAllRobots() {
        WebResource webResource = getClientResource("/robot/list");
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            GetAllRobotResponse robotResponse = response.getEntity(GetAllRobotResponse.class);
            return robotResponse.getList();
        } else {
            ErrorResponse errorResponse = response.getEntity(ErrorResponse.class);
            Logger.error(errorResponse.getMessage());
            return null;
        }
    }

    private WebResource getClientResource(String path) {
        Client jerseyClient = Client.create();
        return jerseyClient.resource(API_URL + path);
    }
}

