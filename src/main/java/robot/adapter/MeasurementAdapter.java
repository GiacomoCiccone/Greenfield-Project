package robot.adapter;

import common.json.MeasurementJsonSchema;
import robot.simulator.Measurement;

public class MeasurementAdapter {

    public static MeasurementJsonSchema adapt(Measurement measurement) {
        MeasurementJsonSchema jsonSchema = new MeasurementJsonSchema();
        jsonSchema.setValue(measurement.getValue());
        jsonSchema.setTimestamp(measurement.getTimestamp());
        return jsonSchema;
    }
}
