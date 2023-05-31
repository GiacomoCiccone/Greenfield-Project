package robot.task;

import robot.command.CommandScheduler;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private final List<RobotTask> tasks;
    private final CommandScheduler scheduler;


    public TaskManager(CommandScheduler scheduler) {
        tasks = new ArrayList<>();
        this.scheduler = scheduler;
    }

    public void startTasks() {
        stopAllTasksAndClear();
        tasks.add(new SensorDataReader());
        tasks.add(new SensorDataPublisher());
        tasks.add(new UserInputReader(scheduler));
        tasks.add(new FaultSimulator(scheduler));
        startAllTasks();
    }

    private void startAllTasks() {
        for (RobotTask task : tasks) {
            task.start();
        }
    }

    public void stopAllTasksAndClear() {
        for (RobotTask task : tasks) {
            task.stop();
        }
        tasks.clear();
    }
}
