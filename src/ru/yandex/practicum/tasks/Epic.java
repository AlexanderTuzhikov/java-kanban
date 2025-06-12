package ru.yandex.practicum.tasks;

import java.util.HashMap;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtaskForEpic = new HashMap<>();

    public Epic(int taskId, String taskName, String taskInfo) {
        super(taskId, taskName, taskInfo);
    }


    public HashMap<Integer, Subtask> getSubtaskForEpic() {
        return subtaskForEpic;
    }
    public void setSubtaskForEpic(int taskId, Subtask subtask) {
        subtaskForEpic.put(taskId,subtask);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Epic epic)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(subtaskForEpic, epic.subtaskForEpic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskForEpic);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "taskName='" + getTaskName() + '\'' +
                ", taskInfo='" + getTaskInfo() + '\'' +
                ", taskId=" + getTaskId() +
                ", status=" + getStatus() +
                '}';
    }
}
