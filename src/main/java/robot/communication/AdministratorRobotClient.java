package robot.communication;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import common.request.RobotInitializationRequest;
import common.response.ErrorResponse;
import common.response.RobotInitializationResponse;
import robot.exception.ServerRequestException;
import common.utils.Logger;

import javax.ws.rs.core.Response;

public class AdministratorRobotClient {

    private final Client client;
    private static String serverAddress = "";

    public AdministratorRobotClient() {
        this.client = Client.create();
    }

    public static void setServerAddress(String address) {
        serverAddress = address;
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

    public void removeRobot(String id) throws ServerRequestException {
        Logger.info("Sending request to server to remove robot.");

        WebResource webResource = client.resource(serverAddress + "/robot/remove/" + id);
        ClientResponse response = webResource.type("application/json").delete(ClientResponse.class);

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            ErrorResponse errorResponse = response.getEntity(ErrorResponse.class);

            throw new ServerRequestException(errorResponse.getMessage());
        }

        Logger.info("Robot removed successfully.");
    }

    public void unregisterSelf(String id) throws ServerRequestException {
        Logger.info("Sending request to server to unregister robot.");

        WebResource webResource = client.resource(serverAddress + "/robot/remove/" + id);
        ClientResponse response = webResource.type("application/json").delete(ClientResponse.class);

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            ErrorResponse errorResponse = response.getEntity(ErrorResponse.class);

            throw new ServerRequestException(errorResponse.getMessage());
        }

        Logger.info("Robot unregistered successfully.");
    }
}
