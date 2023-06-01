package administrator.client.logic;

import administrator.client.Exception.ResponseException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import common.bean.RobotInfoBean;
import common.response.ErrorResponse;
import common.response.GetAllRobotResponse;
import common.utils.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class RobotAPIClient {
    private static final String API_URL = "http://localhost:8080";

    public List<RobotInfoBean> getAllRobots() throws ResponseException {
        Logger.debug("Sending request to get robot list");

        WebResource webResource = getClientResource("/robot/list");
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            GetAllRobotResponse robotResponse = response.getEntity(GetAllRobotResponse.class);
            return robotResponse.getList();
        } else {
            ErrorResponse errorResponse = response.getEntity(ErrorResponse.class);
            throw new ResponseException(errorResponse.getMessage());
        }
    }

    private WebResource getClientResource(String path) {
        Client jerseyClient = Client.create();
        return jerseyClient.resource(API_URL + path);
    }
}

