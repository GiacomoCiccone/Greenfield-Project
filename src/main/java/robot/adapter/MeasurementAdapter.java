package robot.adapter;

import common.json.RobotPollutionDataJsonSchema;
import robot.simulator.Measurement;

public class MeasurementAdapter {
    public static RobotPollutionDataJsonSchema.MeasurementJsonSchema adapt(Measurement measurement) {
        RobotPollutionDataJsonSchema.MeasurementJsonSchema jsonSchema = new RobotPollutionDataJsonSchema.MeasurementJsonSchema();
        jsonSchema.setValue(measurement.getValue());
        jsonSchema.setTimestamp(measurement.getTimestamp());
        return jsonSchema;
    }
}
