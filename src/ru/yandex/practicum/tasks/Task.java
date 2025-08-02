package ru.yandex.practicum.tasks;

import java.util.Objects;

public class Task {

    private final int taskId;
    protected Type type;
    protected String taskName;
    protected TaskStatus status;
    protected String taskInfo;

    public Task(int taskId, String taskName, String taskInfo) {
        this.taskId = taskId;
        type = Type.TASK;
        this.taskName = taskName;
        this.taskInfo = taskInfo;
        status = TaskStatus.NEW;
    }

    public Task(int taskId, Type type, String taskName, TaskStatus taskStatus, String taskInfo) {
        this.taskId = taskId;
        this.type = type;
        this.taskName = taskName;
        this.status = taskStatus;
        this.taskInfo = taskInfo;

    }

    public Type getType() {
        return type;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskInfo() {
        return taskInfo;
    }

    public void setTaskInfo(String taskInfo) {
        this.taskInfo = taskInfo;
    }

    public int getTaskId() {
        return taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task task)) return false;
        return taskId == task.taskId
                && Objects.equals(taskName, task.taskName)
                && Objects.equals(taskInfo, task.taskInfo)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, taskInfo, taskId, status);
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s", taskId, type, taskName, status, taskInfo);
    }
}