package ru.yandex.practicum.tasks;

import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int taskId, String taskName, String taskInfo, int epicId) {
        super(taskId, taskName, taskInfo);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Subtask subtask)) return false;
        if (!super.equals(o)) return false;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "taskName='" + getTaskName() + '\'' +
                ", taskInfo='" + getTaskInfo() + '\'' +
                ", taskId=" + getTaskId() +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                '}';
    }
}
