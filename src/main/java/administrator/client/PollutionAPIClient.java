package administrator.client;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import common.response.AveragePollutionValueResponse;
import common.response.ErrorResponse;
import utils.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class PollutionAPIClient {
    private static final String API_URL = "http://localhost:8080";

    public AveragePollutionValueResponse getLastPollutionAverages(String n, String robotId) {
        WebResource webResource = getClientResource("/pollution/last/" + n + "/" + robotId);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getEntity(AveragePollutionValueResponse.class);
        } else {
            ErrorResponse errorResponse = response.getEntity(ErrorResponse.class);
            Logger.error(errorResponse.getMessage());
            return null;
        }
    }

    public AveragePollutionValueResponse getPollutionDataByTimestamp(String t1, String t2) {
        WebResource webResource = getClientResource("/pollution/timestamp/" + t1 + "/" + t2);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getEntity(AveragePollutionValueResponse.class);
        } else {
            ErrorResponse errorResponse = response.getEntity(ErrorResponse.class);
            Logger.error(errorResponse.getMessage());
            return null;
        }
    }

    private WebResource getClientResource(String path) {
        com.sun.jersey.api.client.Client jerseyClient = com.sun.jersey.api.client.Client.create();
        return jerseyClient.resource(API_URL + path);
    }
}

