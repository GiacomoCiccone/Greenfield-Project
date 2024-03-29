package administrator.client.logic;

import administrator.client.Exception.ResponseException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import common.response.AveragePollutionValueResponse;
import common.response.ErrorResponse;
import common.utils.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class PollutionAPIClient {
    private static final String API_URL = "http://localhost:8080";

    public AveragePollutionValueResponse getLastPollutionAverages(String n, String robotId) throws ResponseException {
        Logger.info("Sending request to get last " + n + " pollution data of robot " + robotId);

        WebResource webResource = getClientResource("/pollution/last/" + n + "/" + robotId);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getEntity(AveragePollutionValueResponse.class);
        } else {
            ErrorResponse errorResponse = response.getEntity(ErrorResponse.class);
            throw new ResponseException(errorResponse.getMessage());
        }
    }

    public AveragePollutionValueResponse getPollutionDataByTimestamp(String t1, String t2) throws ResponseException {
        Logger.info("Sending request to get pollution data between " + t1 + " and " + t2);

        WebResource webResource = getClientResource("/pollution/timestamp/" + t1 + "/" + t2);
        ClientResponse response = webResource.type(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            return response.getEntity(AveragePollutionValueResponse.class);
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
