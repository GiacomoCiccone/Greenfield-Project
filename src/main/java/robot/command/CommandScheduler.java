package robot.command;

import utils.Logger;

public class CommandScheduler {
    private String currentCommand;

    public synchronized void schedule(String command) {
        while (currentCommand != null) {
            try {
                wait();
            } catch (InterruptedException e) {
                Logger.logException(e);
                return;
            }
        }

        currentCommand = command;
        notifyAll();
    }

    public synchronized String getNextCommand() throws InterruptedException {
        while (currentCommand == null) {
            wait();
        }
        return currentCommand;
    }

    public synchronized void clearCommand() {
        currentCommand = null;
        notifyAll();
    }


}