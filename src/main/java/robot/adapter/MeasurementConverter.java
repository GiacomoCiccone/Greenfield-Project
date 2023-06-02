package robot.adapter;

import common.json.RobotPollutionDataJsonSchema;
import robot.simulator.Measurement;

public class MeasurementConverter {
    public static RobotPollutionDataJsonSchema.MeasurementJsonSchema convert(Measurement measurement) {
        RobotPollutionDataJsonSchema.MeasurementJsonSchema jsonSchema = new RobotPollutionDataJsonSchema.MeasurementJsonSchema();
        jsonSchema.setValue(measurement.getValue());
        jsonSchema.setTimestamp(measurement.getTimestamp());
        return jsonSchema;
    }
}
