package administrator.server.adapter;

import administrator.server.model.PollutionDataEntity;
import common.json.RobotPollutionDataJsonSchema;

import java.util.List;
import java.util.stream.Collectors;

public class PollutionDataEntityAdapter {
    public static PollutionDataEntity adapt(RobotPollutionDataJsonSchema robotPollutionDataJsonSchema) {
        String robotId = robotPollutionDataJsonSchema.getRobotId();
        List<RobotPollutionDataJsonSchema.MeasurementJsonSchema> pollutionData = robotPollutionDataJsonSchema.getPollutionData();

        List<PollutionDataEntity.Measurement> pollutionDataAdapted = pollutionData.stream()
                .map(PollutionDataEntityAdapter::adapt)
                .collect(Collectors.toList());

        long timestamp = robotPollutionDataJsonSchema.getTimestamp();

        return new PollutionDataEntity(robotId, pollutionDataAdapted, timestamp);
    }

    private static PollutionDataEntity.Measurement adapt(RobotPollutionDataJsonSchema.MeasurementJsonSchema measurementJsonSchema) {
        double value = measurementJsonSchema.getValue();
        long timestamp = measurementJsonSchema.getTimestamp();

        return new PollutionDataEntity.Measurement(value, timestamp);
    }
}
