package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.*;
import ru.yandex.practicum.util.Managers;

import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {

    HistoryManager historyManager = Managers.getDefaultHistory();

    private final HashMap<Integer, Task> taskList = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    private final HashMap<Integer, Epic> epicList = new HashMap<>();

    private int taskId = 1;

    @Override
    public Task createNewTask(String taskName, String taskInfo) {
        Task task = new Task(taskId, taskName, taskInfo);
        putTask(task);
        taskId++;
        return task;
    }
    @Override
    public Subtask createNewSubtask(String taskName, String taskInfo, int epicId) {
        if(epicList.containsKey(epicId)) {
        Subtask subtask = new Subtask(taskId, taskName, taskInfo, epicId);
        putSubtask(subtask);
        Epic epicForSubtask = getEpicById(epicId);
        epicForSubtask.setSubtaskForEpic(taskId, subtask);
        taskId++;
        return subtask;
        } else {
            System.out.println("Epic с ID: " + epicId + " не создан или удален");
            return null;
        }
    }
    @Override
    public Epic createNewEpic(String epicName, String epicInfo) {
        Epic epic = new Epic(taskId, epicName, epicInfo);
        putEpic(epic);
        taskId++;
        return epic;
    }

    @Override
    public void putTask(Task task) {
        taskList.put(task.getTaskId(), task);
    }
    @Override
    public void putSubtask(Subtask subtask) {
        subtaskList.put(subtask.getTaskId(), subtask);
    }
    @Override
    public void putEpic(Epic epic) {
        epicList.put(epic.getTaskId(), epic);
    }

    @Override
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(taskList.values());
    }
    @Override
    public ArrayList<Subtask> getAllSubtaskTask() {
        return new ArrayList<>(subtaskList.values());
    }
    @Override
    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epicList.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = taskList.get(id);
        Task taskVersion = new Task(task.getTaskId(), task.getTaskName(), task.getTaskInfo());
        taskVersion.setStatus(task.getStatus());
        historyManager.updateHistory(taskVersion);
        return taskList.get(id);
    }
    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtaskList.get(id);
        Subtask subtaskVersion = new Subtask(subtask.getTaskId(), subtask.getTaskName(), subtask.getTaskInfo(), subtask.getEpicId());
        subtaskVersion.setStatus(subtask.getStatus());
        historyManager.updateHistory(subtaskVersion);
        return subtaskList.get(id);
    }
    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicList.get(id);
        Epic epicVersion = new Epic(epic.getTaskId(), epic.getTaskName(), epic.getTaskInfo());
        epicVersion.setStatus(epic.getStatus());
        historyManager.updateHistory(epicVersion);
        return epicList.get(id);
    }

    @Override
    public HashMap<Integer, Subtask> getAllEpicSubtask(Epic epic) {
            return epic.getSubtaskForEpic();
    }

    @Override
    public void updateTask(Task task, TaskStatus status) {
        task.setStatus(status);
        taskList.put(task.getTaskId(), task);
    }
    @Override
    public void updateSubtask(Subtask subtask, TaskStatus status) {
        subtask.setStatus(status);
        subtaskList.put(subtask.getTaskId(), subtask);
        updateEpic(getEpicById(subtask.getEpicId()));
    }
    @Override
    public void updateEpic(Epic epic) {
        int statusDone = 0;
        int statusInProgress = 0;

        for (Subtask subtask : getAllEpicSubtask(epic).values()) {
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                statusInProgress++;
            } else if (subtask.getStatus() == TaskStatus.DONE) {
                statusDone++;
            }
        }
        if (getAllEpicSubtask(epic).isEmpty()) {
        epic.setStatus(TaskStatus.NEW);
        } else if (statusDone == getAllEpicSubtask(epic).size()) {
            epic.setStatus(TaskStatus.DONE);
        } else if (statusInProgress > 0
                || statusDone > 0) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
        epicList.put(epic.getTaskId(), epic);
    }

    @Override
    public void removeAllTask() {
        taskList.clear();
    }
    @Override
    public void removeAllSubtask() {
        for(Epic epic : epicList.values()) {
            epic.getSubtaskForEpic().clear();
        }
        subtaskList.clear();
        for (Epic epic : epicList.values()) {
            updateEpic(epic);
        }
    }
    @Override
    public void removeAllEpic() {
        epicList.clear();
        removeAllSubtask();
    }

    @Override
    public void removeTaskById(int id) {
        taskList.remove(id);
    }
    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = getSubtaskById(id);
        int epicId = subtask.getEpicId();

        getEpicById(epicId).getSubtaskForEpic().remove(id);
        subtaskList.remove(id);
        updateEpic(getEpicById(epicId));
    }
    @Override
    public void removeEpicById(int id) {
        epicList.remove(id);

        ArrayList<Integer> idSubtaskForRemove = new ArrayList<>();
        for (Subtask subtask: subtaskList.values()) {
            if(subtask.getEpicId() == id) {
                idSubtaskForRemove.add(subtask.getTaskId());
            }
        }

        for (int idForRemove : idSubtaskForRemove) {
            subtaskList.remove(idForRemove);
        }

    }

}

