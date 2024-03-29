package common.json;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class RobotPollutionDataJsonSchema {

    String robotId;
    List<MeasurementJsonSchema> pollutionData;
    long timestamp;

    public RobotPollutionDataJsonSchema() {
    }

    public RobotPollutionDataJsonSchema(String robotId, List<MeasurementJsonSchema> pollutionData, long timestamp) {
        this.robotId = robotId;
        this.pollutionData = pollutionData;
        this.timestamp = timestamp;
    }

    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public List<MeasurementJsonSchema> getPollutionData() {
        return pollutionData;
    }

    public void setPollutionData(List<MeasurementJsonSchema> pollutionData) {
        this.pollutionData = pollutionData;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    @XmlRootElement
    public static class MeasurementJsonSchema {

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
}
