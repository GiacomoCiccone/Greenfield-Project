package administrator.server.api;

import administrator.server.exception.NotFoundException;
import administrator.server.dao.PollutionDao;
import administrator.server.model.PollutionDataEntity;
import common.response.AveragePollutionValueResponse;
import common.response.ErrorResponse;
import utils.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/pollution")
public class PollutionAPI {

    @GET
    @Path("/last/{n}/{robotId}")
    @Produces("application/json")
    public Response getAveragePollutionDataOfRobot(@PathParam("n") int n, @PathParam("robotId") String robotId, @Context HttpHeaders headers) {
        HostInfo hostInfo = HostInfo.parseHostInfo(headers);
        Logger.info("Received request to get average pollution data of robot from " + hostInfo.getAddress() + ":" + hostInfo.getPort());

        // Validate request
        if (n <= 0) {
            ErrorResponse errorResponse = new ErrorResponse("Invalid parameter: n must be greater than 0", Response.Status.BAD_REQUEST.getStatusCode());

            Logger.debug("Invalid parameter: n must be greater than 0");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }

        PollutionDao pollutionDao = new PollutionDao();

        List<PollutionDataEntity> pollutionAnalysisData = null;
        try {
            pollutionAnalysisData = pollutionDao.getLastNPollutionEntriesByRobotId(n, robotId);
        } catch (NotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), Response.Status.NOT_FOUND.getStatusCode());

            Logger.debug("Not enough pollution data found for robot with id " + robotId);
            return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
        }

        double sum = 0.0;
        double totalSample = 0;

        for (PollutionDataEntity entry : pollutionAnalysisData) {
            for (PollutionDataEntity.Measurement measurement : entry.getPollutionData()) {
                sum += measurement.getValue();
                totalSample++;
            }
        }

        double average = sum / totalSample;

        Logger.debug("Returning average pollution data");
        return Response.ok(new AveragePollutionValueResponse(average, (int) totalSample)).build();
    }

    @GET
    @Path("/timestamp/{t1}/{t2}")
    @Produces("application/json")
    public Response getAveragePollutionDataBetweenTimestamps(@PathParam("t1") long t1, @PathParam("t2") long t2, @Context HttpHeaders headers) {
        HostInfo hostInfo = HostInfo.parseHostInfo(headers);
        Logger.info("Received request to get average pollution data between timestamps from " + hostInfo.getAddress() + ":" + hostInfo.getPort());

        // Validate request
        if (t1 < 0 || t2 < 0 || t1 > t2) {
            ErrorResponse errorResponse = new ErrorResponse("Invalid parameters: t1 and t2 must be greater than 0 and t1 must be less than t2", Response.Status.BAD_REQUEST.getStatusCode());

            Logger.debug("Invalid parameters: t1 and t2 must be greater than 0 and t1 must be less than t2");
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }

        PollutionDao pollutionDao = new PollutionDao();

        List<PollutionDataEntity> pollutionAnalysisData = pollutionDao.getAllPollutionEntriesInInterval(t1, t2);

        if (pollutionAnalysisData.isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("No pollution data found in the given interval", Response.Status.NOT_FOUND.getStatusCode());

            Logger.debug("No pollution data found in the given interval");
            return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
        }

        double sum = 0.0;
        int totalSample = 0;

        for (PollutionDataEntity entry : pollutionAnalysisData) {
            for (PollutionDataEntity.Measurement measurement : entry.getPollutionData()) {
                sum += measurement.getValue();
                totalSample++;
            }
        }


        double average = sum / totalSample;

        Logger.debug("Returning average pollution data");
        return Response.ok(new AveragePollutionValueResponse(average, (int) totalSample)).build();
    }
}
