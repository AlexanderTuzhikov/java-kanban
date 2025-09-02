package ru.yandex.practicum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtaskForEpic = new HashMap<>();
    private LocalDateTime endTime;

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Epic(String taskName, String taskInfo, TaskStatus status) {
        super(taskName, taskInfo, status);
        this.type = Type.EPIC;
    }

    public HashMap<Integer, Subtask> getSubtaskForEpic() {
        return subtaskForEpic;
    }

    public void setSubtaskForEpic(int taskId, Subtask subtask) {
        if (subtaskForEpic == null) {
            subtaskForEpic = new HashMap<>();
        }
        subtaskForEpic.put(taskId, subtask);
    }

    public void updateStartAndEndTime() {
        if (subtaskForEpic == null || subtaskForEpic.isEmpty()) {
            startTime = null;
            duration = null;
            endTime = null;
            return;
        }

        startTime = subtaskForEpic.values().stream()
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);


        endTime = subtaskForEpic.values().stream()
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);


        duration = subtaskForEpic.values().stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
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
        DateTimeFormatter formatterStart = DateTimeFormatter.ofPattern("Начало: dd.MM.yy HH:mm");
        DateTimeFormatter formatterEnd = DateTimeFormatter.ofPattern("Завершение: dd.MM.yy HH:mm");

        String formatDuration = duration != null ? duration.toHours() + " часов " +
                duration.toMinutesPart() + " минут" : "Выполнение " + "0";
        String formatStartTime = startTime != null ? startTime.format(formatterStart) : "Не установлено";
        String formatEndTime = endTime != null ? endTime.format(formatterEnd) : "Не установлено";

        return String.format("""
                        --------------------------------
                        ID задачи: %s
                        Тип задачи: %s
                        Статус: %s
                        Название: %s
                        Информация: %s
                        Выполнение: %s
                        Начало: %s
                        Конец: %s
                        --------------------------------
                        """,
                getTaskId(),
                type,
                status,
                taskName,
                taskInfo,
                formatDuration,
                formatStartTime,
                formatEndTime);
    }

    @Override
    public String toCsv() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", getTaskId(), type, taskName, status, taskInfo, "null",
                duration, startTime, endTime);
    }
}