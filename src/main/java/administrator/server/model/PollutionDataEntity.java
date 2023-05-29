package administrator.server.model;

import java.util.ArrayList;
import java.util.List;

public class PollutionDataEntity {

    String robotId;
    List<Measurement> pollutionData;
    long timestamp;

    public PollutionDataEntity(String robotId, List<Measurement> pollutionData, long timestamp) {
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

    public List<Measurement> getPollutionData() {
        return pollutionData;
    }

    public void setPollutionData(List<Measurement> pollutionData) {
        this.pollutionData = pollutionData;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static class Measurement {

        double value;
        long timestamp;

        public Measurement(double value, long timestamp) {
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
