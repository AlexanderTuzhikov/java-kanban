package ru.yandex.practicum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {

    private int taskId;
    protected Type type;
    protected String taskName;
    protected TaskStatus status;
    protected String taskInfo;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String taskName, String taskInfo, TaskStatus status) {
        type = Type.TASK;
        this.taskName = taskName;
        this.taskInfo = taskInfo;
        this.status = status;
    }

    public Task(String taskName, String taskInfo, TaskStatus status, LocalDateTime startTime, Duration duration) {
        type = Type.TASK;
        this.taskName = taskName;
        this.taskInfo = taskInfo;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) return null;
        return startTime.plusMinutes(duration.toMinutes());
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
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

    public void setTaskId(int taskId) {
        this.taskId = taskId;
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
        DateTimeFormatter formatterStart = DateTimeFormatter.ofPattern("Начало: dd.MM.yy HH:mm");
        DateTimeFormatter formatterEnd = DateTimeFormatter.ofPattern("Завершение: dd.MM.yy HH:mm");

        String formatDuration = duration != null ? duration.toHours() + " часов " +
                duration.toMinutesPart() + " минут" : "Выполнение " + "0";
        String formatStartTime = startTime != null ? startTime.format(formatterStart) : "Не установлено";
        String formatEndTime = getEndTime() != null ? getEndTime().format(formatterEnd) : "Не установлено";

        return String.format("""
                        --------------------------------
                        ID задачи: %s
                        Тип задачи: %s
                        Статус: %s
                        Название: %s
                        Информация:%s
                        Выполнение: %s
                        Начало: %s
                        Конец: %s
                        --------------------------------
                        """,
                taskId,
                type,
                status,
                taskName,
                taskInfo,
                formatDuration,
                formatStartTime,
                formatEndTime);
    }

    public String toCvs() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", taskId, type, taskName, status, taskInfo, "null",
                duration, startTime, getEndTime());
    }
}