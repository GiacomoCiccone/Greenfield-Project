package robot.state;

import robot.exception.IllegalStateTransitionException;
import utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class RobotState {

    private boolean isRunning = false;
    private boolean isBroken = false;
    private boolean isFixing = false;

    public void turnOn() {
        Logger.info("Robot has been turned on");
        if (getState() != StateType.OFF) {
            throw new IllegalStateTransitionException("Cannot turn on robot when it is not off");
        }
        isRunning = true;
        isBroken = false;
        isFixing = false;
    }

    public void turnOff() {
        Logger.info("Robot has been turned off");
        if (getState() != StateType.RUNNING) {
            throw new IllegalStateTransitionException("Cannot turn off robot when it is not running");
        }
        isRunning = false;
        isBroken = false;
        isFixing = false;
    }

    public void faultOccurred() {
        Logger.info("Robot is broken");
        if (getState() != StateType.RUNNING) {
            throw new IllegalStateTransitionException("Cannot fault robot when it is not running");
        }
        isBroken = true;
        isRunning = true;
        isFixing = false;
    }

    public void enteredMechanic() {
        Logger.info("Robot entered mechanic");
        if (getState() != StateType.BROKEN) {
            throw new IllegalStateTransitionException("Cannot enter mechanic when robot is not broken");
        }
        isFixing = true;
        isBroken = true;
        isRunning = true;
    }

    public void leftMechanic() {
        Logger.info("Robot left mechanic");
        if (getState() != StateType.FIXING) {
            throw new IllegalStateTransitionException("Cannot leave mechanic when robot is not fixing");
        }
        isRunning = true;
        isFixing = false;
        isBroken = false;
    }

    public synchronized StateType getState() {
        if (isRunning) {
            if (isBroken) {
                if (isFixing) {
                    return StateType.FIXING;
                } else {
                    return StateType.BROKEN;
                }
            } else {
                return StateType.RUNNING;
            }
        } else {
            return StateType.OFF;
        }
    }

}
