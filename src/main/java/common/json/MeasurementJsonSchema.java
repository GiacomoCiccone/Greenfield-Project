package common.json;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MeasurementJsonSchema {

    double value;

    long timestamp;

    public MeasurementJsonSchema() {
    }

    public MeasurementJsonSchema(double value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
