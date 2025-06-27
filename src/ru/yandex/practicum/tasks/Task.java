package ru.yandex.practicum.tasks;

import java.util.Objects;

public class Task {

    private String taskName;
    private String taskInfo;
    private final int taskId;
    private TaskStatus status;

    public Task(int taskId, String taskName, String taskInfo) {
        this.taskName = taskName;
        this.taskInfo = taskInfo;
        this.taskId = taskId;
        status = TaskStatus.NEW;
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
        return "Task {" +
                "taskName='" + taskName + '\'' +
                ", taskInfo='" + taskInfo + '\'' +
                ", taskId=" + taskId +
                ", status=" + status +
                "}\n";
    }
}