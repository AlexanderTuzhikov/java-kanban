package ru.yandex.practicum.tasks;

import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int taskId, String taskName, String taskInfo, int epicId) {
        super(taskId, taskName, taskInfo);
        this.epicId = epicId;
        this.type = Type.SUBTASK;
    }

    public Subtask(int taskId, Type type, String taskName, TaskStatus status, String taskInfo, int epicId) {
        super(taskId, type, taskName, status, taskInfo);
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
        return String.format("%s,%s,%s,%s,%s,%s", getTaskId(), type, taskName, status, taskInfo, epicId);
    }
}