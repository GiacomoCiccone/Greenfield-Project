package common.response;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AveragePollutionValueResponse {
    double value;
    int totalSample;

    public AveragePollutionValueResponse() {
    }

    public AveragePollutionValueResponse(double value, int totalSample) {
        this.value = value;
        this.totalSample = totalSample;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getTotalSample() {
        return totalSample;
    }

    public void setTotalSample(int totalSample) {
        this.totalSample = totalSample;
    }
}
