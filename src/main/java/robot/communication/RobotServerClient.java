package robot.communication;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import common.request.RobotInitializationRequest;
import common.response.ErrorResponse;
import common.response.RobotInitializationResponse;
import robot.exception.ServerRequestException;
import utils.Logger;

import javax.ws.rs.core.Response;

public class RobotServerClient {

    private final Client client;
    private final String serverAddress;

    public RobotServerClient(String serverAddress) {
        this.serverAddress = serverAddress;
        this.client = Client.create();
    }

    public RobotInitializationResponse initializeRobot(String id, String address, int port) throws ServerRequestException {
        Logger.info("Sending request to server to initialize robot.");

        WebResource webResource = client.resource(serverAddress + "/robot/add");
        RobotInitializationRequest request = new RobotInitializationRequest(id, address, port);
        ClientResponse response = webResource.type("application/json").entity(request).post(ClientResponse.class);

        if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
            ErrorResponse errorResponse = response.getEntity(ErrorResponse.class);

            throw new ServerRequestException(errorResponse.getMessage());
        }

        Logger.info("Response received from server successfully.");
        return response.getEntity(RobotInitializationResponse.class);
    }
}
