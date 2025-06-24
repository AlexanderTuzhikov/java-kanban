package ru.yandex.practicum.tasks;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {

    Task createNewTask(String taskName, String taskInfo);
    Subtask createNewSubtask(String taskName, String taskInfo, int epicId);
    Epic createNewEpic(String epicName, String epicInfo);

    void putTask(Task task);
    void putSubtask(Subtask subtask);
    void putEpic(Epic epic);

    ArrayList<Task> getAllTask();
    ArrayList<Subtask> getAllSubtaskTask();
    ArrayList<Epic> getAllEpic();

    Task getTaskById(int id);
    Subtask getSubtaskById(int id);
    Epic getEpicById(int id);

    HashMap<Integer, Subtask> getAllEpicSubtask(Epic epic);

    void updateTask(Task task, TaskStatus status);
    void updateSubtask(Subtask subtask, TaskStatus status);
    void updateEpic(Epic epic);

    void removeAllTask();
    void removeAllSubtask();
    void removeAllEpic();

    void removeTaskById(int id);
    void removeSubtaskById(int id);
    void removeEpicById(int id);
}
