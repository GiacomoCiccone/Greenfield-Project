package robot.task;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private final List<RobotTask> tasks;

    public TaskManager() {
        tasks = new ArrayList<>();
    }

    public void addTaskAndStart(RobotTask task) {
        tasks.add(task);
        task.start();
    }

    public void stopAllTasksAndClear() {
        for (RobotTask task : tasks) {
            task.stop();
        }
        tasks.clear();
    }
}
