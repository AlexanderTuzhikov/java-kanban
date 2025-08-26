package ru.yandex.practicum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String taskName, String taskInfo, TaskStatus status, int epicId) {
        super(taskName, taskInfo, status);
        this.epicId = epicId;
        this.type = Type.SUBTASK;
    }

    public Subtask(String taskName, String taskInfo, TaskStatus status, int epicId,
                   LocalDateTime startTime, Duration duration) {
        super(taskName, taskInfo, status, startTime, duration);
        type = Type.SUBTASK;
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
        DateTimeFormatter formatterStart = DateTimeFormatter.ofPattern("Начало: dd.MM.yy HH:mm");
        DateTimeFormatter formatterEnd = DateTimeFormatter.ofPattern("Завершение: dd.MM.yy HH:mm");

        String formatDuration = duration != null ? duration.toHours() + " часов " +
                duration.toMinutesPart() + " минут" : "Выполнение " + "0";
        String formatStartTime = startTime != null ? startTime.format(formatterStart) : "Не установлено";
        String formatEndTime = getEndTime() != null ? getEndTime().format(formatterEnd) : "Не установлено";

        return String.format("""
                        --------------------------------
                        ID задачи: %s
                        ID Epic: %s
                        Тип задачи: %s
                        Статус: %s
                        Название: %s
                        Информация:%s
                        Выполнение: %s
                        Начало: %s
                        Конец: %s
                        --------------------------------
                        """,
                getTaskId(),
                epicId,
                type,
                status,
                taskName,
                taskInfo,
                formatDuration,
                formatStartTime,
                formatEndTime);
    }

    @Override
    public String toCvs() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", getTaskId(), type, taskName, status, taskInfo, epicId,
                duration, startTime, getEndTime());
    }
}