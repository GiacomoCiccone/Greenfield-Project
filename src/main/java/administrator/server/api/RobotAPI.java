package administrator.server.api;

import administrator.server.exception.DataIntegrityViolationException;
import administrator.server.exception.NotFoundException;
import administrator.server.exception.InvalidRequestParametersException;
import administrator.server.adapter.RobotEntityAdapter;
import administrator.server.dao.RobotDao;
import administrator.server.model.RobotEntity;
import administrator.server.validator.RobotInitializationRequestValidator;
import common.bean.RobotInfoBean;
import common.request.RobotInitializationRequest;
import common.response.ErrorResponse;
import common.response.GetAllRobotResponse;
import common.response.RobotInitializationResponse;
import common.utils.Greenfield;
import common.utils.Position;
import common.utils.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/robot")
public class RobotAPI {

    @GET
    @Path("/list")
    @Produces("application/json")
    public Response getRobotList(@Context HttpHeaders headers) {
        HostInfo hostInfo = HostInfo.parseHostInfo(headers);
        Logger.info("Received request to get robot list from " + hostInfo.getAddress() + ":" + hostInfo.getPort());

        RobotDao robotDao = new RobotDao();
        List<RobotEntity> otherRobots = robotDao.getAllRobots();

        if (otherRobots.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("No robots found", Response.Status.NOT_FOUND.getStatusCode());

            Logger.debug("No robots found");
            return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
        }

        List<RobotInfoBean> list = otherRobots.stream()
                .map(RobotEntityAdapter::adapt)
                .collect(Collectors.toList());

        Logger.debug("Returning robot list");
        return Response.ok(new GetAllRobotResponse(list)).build();
    }

    @POST
    @Path("/add")
    @Produces("application/json")
    @Consumes("application/json")
    public Response addRobot(RobotInitializationRequest request) {
        Logger.info("Received request to add robot from " + request.getAddress() + ":" + request.getPort());

        // Validate request
        try {
            RobotInitializationRequestValidator.validate(request);
        } catch (InvalidRequestParametersException e) {
            int statusCode = Response.Status.BAD_REQUEST.getStatusCode();
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), statusCode);

            Logger.debug(e.getMessage());
            return Response.status(statusCode).entity(errorResponse).build();
        }

        RobotDao robotDao = new RobotDao();

        // Acquire lock
        robotDao.acquireLock();

        if (robotDao.getRobotById(request.getId()).isPresent()) {
            robotDao.releaseLock();
            int statusCode = Response.Status.CONFLICT.getStatusCode();
            ErrorResponse errorResponse = new ErrorResponse("Robot with id " + request.getId() + " already exists", statusCode);

            Logger.debug("Robot with id " + request.getId() + " already exists");
            return Response.status(statusCode).entity(errorResponse).build();
        }

        // Calculate position
        int district = robotDao.getLessPopulatedDistrict();
        Position position = Greenfield.getRandomPositionInsideDistrict(district);

        // Get other robots
        List<RobotEntity> otherRobots = robotDao.getAllRobots();

        // Add robot
        RobotEntity robotEntity = new RobotEntity(request.getId(), request.getAddress(), request.getPort(), position);
        try {
            robotDao.addRobot(robotEntity);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(e);
        }

        // Release lock
        robotDao.releaseLock();


        // Adapt to response format
        List<RobotInfoBean> otherRobotInfos = otherRobots.stream()
                .map(RobotEntityAdapter::adapt)
                .collect(Collectors.toList());

        // Create response
        RobotInitializationResponse response = new RobotInitializationResponse(position.getX(), position.getY(), otherRobotInfos);


        Logger.debug("Robot with id " + request.getId() + " added successfully");
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @DELETE
    @Path("/remove/{robotId}")
    @Produces("application/json")
    public Response removeRobot(@PathParam("robotId") String robotId, @Context HttpHeaders headers) {
        HostInfo hostInfo = HostInfo.parseHostInfo(headers);
        Logger.info("Received request to remove robot from " + hostInfo.getAddress() + ":" + hostInfo.getPort());

        RobotDao robotDao = new RobotDao();

        try {
            robotDao.removeRobotById(robotId);
        } catch (NotFoundException e) {
            int statusCode = Response.Status.NOT_FOUND.getStatusCode();
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), statusCode);

            Logger.debug("Robot with id " + robotId + " not found");
            return Response.status(statusCode).entity(errorResponse).build();
        }

        Logger.debug("Robot with id " + robotId + " removed successfully");
        return Response.ok().build();
    }

}
