package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface TaskManager {

    List<Task> getPrioritizedTasks();

    void setTimeTask(Task task, LocalDateTime startTime, Duration duration);

    Task createNewTask(String taskName, String taskInfo);

    Subtask createNewSubtask(String taskName, String taskInfo, int epicId);

    Epic createNewEpic(String epicName, String epicInfo);

    void putTask(Task task);

    void putSubtask(Subtask subtask);

    void putEpic(Epic epic);

    ArrayList<Task> getAllTask();

    ArrayList<Subtask> getAllSubtaskTask();

    ArrayList<Epic> getAllEpic();

    Optional<Task> getTaskById(int id);

    Optional<Subtask> getSubtaskById(int id);

    Optional<Epic> getEpicById(int id);

    HashMap<Integer, Subtask> getAllEpicSubtask(Epic epic);

    void updateTaskStatus(Task task, TaskStatus status);

    void updateTaskTime(Task task);

    void updateSubtaskStatus(Subtask subtask, TaskStatus status);

    void updateSubtaskTime(Subtask subtask);

    void updateEpicStatus(Epic epic);

    void updateEpicTime(Epic epic);

    void removeAllTask();

    void removeAllSubtask();

    void removeAllEpic();

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);
}