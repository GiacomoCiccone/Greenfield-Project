package robot.core;

public class RobotState {
    private boolean isRunning;
    private boolean needFixing;
    private boolean isFixing;

    public RobotState() {
        this.isRunning = false;
        this.needFixing = false;
        this.isFixing = false;
    }

    public void turnOn() {
        this.isRunning = true;
    }

    public void turnOff() {
        this.isRunning = false;
        this.isFixing = false;
        this.needFixing = false;
    }

    public void faultOccurred() {
        this.needFixing = true;
    }

    public void startFixing() {
        this.needFixing = false;
        this.isFixing = true;
    }

    public void hasBeenFixed() {
        this.needFixing = false;
        this.isFixing = false;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public boolean needFixing() {
        return this.needFixing;
    }

    public boolean isFixing() {
        return this.isFixing;
    }
}
