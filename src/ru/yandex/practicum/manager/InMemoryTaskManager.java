package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.*;
import ru.yandex.practicum.util.Managers;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    protected final Map<Integer, Task> taskList = new HashMap<>();
    protected final Map<Integer, Subtask> subtaskList = new HashMap<>();
    protected final Map<Integer, Epic> epicList = new HashMap<>();

    protected int taskId = 1;

    @Override
    public Task createNewTask(String taskName, String taskInfo) {
        taskId++;
        Task task = new Task(taskId, taskName, taskInfo);
        putTask(task);
        return task;
    }

    @Override
    public Subtask createNewSubtask(String taskName, String taskInfo, int epicId) {
        if (!epicList.containsKey(epicId)) {
            return null;
        }

        taskId++;
        Subtask subtask = new Subtask(taskId, taskName, taskInfo, epicId);
        putSubtask(subtask);
        Epic epic = epicList.get(epicId);
        epic.setSubtaskForEpic(taskId, subtask);
        return subtask;
    }

    @Override
    public Epic createNewEpic(String epicName, String epicInfo) {
        taskId++;
        Epic epic = new Epic(taskId, epicName, epicInfo);
        putEpic(epic);
        return epic;
    }

    @Override
    public void putTask(Task task) {
        if (task != null) {
            int id = task.getTaskId();
            taskList.put(id, task);
        }
    }

    @Override
    public void putSubtask(Subtask subtask) {
        if (subtask != null) {
            int id = subtask.getTaskId();
            subtaskList.put(id, subtask);
        }
    }

    @Override
    public void putEpic(Epic epic) {
        if (epic != null) {
            int id = epic.getTaskId();
            epicList.put(id, epic);
        }
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

        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtaskList.get(id);

        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicList.get(id);

        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public HashMap<Integer, Subtask> getAllEpicSubtask(Epic epic) {
        if (epic != null) {
            return epic.getSubtaskForEpic();
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public void updateTask(Task task, TaskStatus status) {
        if (task != null && status != null) {
            task.setStatus(status);
            int id = task.getTaskId();
            taskList.put(id, task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask, TaskStatus status) {
        if (subtask != null && status != null) {
            subtask.setStatus(status);

            int idSubtask = subtask.getTaskId();
            subtaskList.put(idSubtask, subtask);

            int idEpic = subtask.getEpicId();
            Epic epic = epicList.get(idEpic);
            updateEpic(epic);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            int statusDone = 0;
            int statusInProgress = 0;
            Map<Integer, Subtask> actualSubtask = getAllEpicSubtask(epic);

            if (actualSubtask.isEmpty()) {
                epic.setStatus(TaskStatus.NEW);
                return;
            }

            for (Subtask subtask : actualSubtask.values()) {

                if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                    statusInProgress++;
                } else if (subtask.getStatus() == TaskStatus.DONE) {
                    statusDone++;
                }
            }

            if (statusDone == actualSubtask.size()) {
                epic.setStatus(TaskStatus.DONE);
            } else if (statusInProgress > 0
                    || statusDone > 0) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            } else {
                epic.setStatus(TaskStatus.NEW);
            }

            int id = epic.getTaskId();
            epicList.put(id, epic);
        }
    }

    @Override
    public void removeAllTask() {
        for (int idTask : taskList.keySet()) {
            historyManager.remove(idTask);
        }
        taskList.clear();
    }

    @Override
    public void removeAllSubtask() {
        for (int idTask : subtaskList.keySet()) {
            historyManager.remove(idTask);
        }

        for (Epic epic : epicList.values()) {
            epic.getSubtaskForEpic().clear();
            updateEpic(epic);
        }

        subtaskList.clear();
    }

    @Override
    public void removeAllEpic() {
        for (int idTask : epicList.keySet()) {
            historyManager.remove(idTask);
        }

        epicList.clear();
        removeAllSubtask();
    }

    @Override
    public void removeTaskById(int id) {
        taskList.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtaskList.get(id);
        int epicId = subtask.getEpicId();
        Epic epic = epicList.get(epicId);

        Map<Integer, Subtask> subtasks = epic.getSubtaskForEpic();
        subtasks.remove(id);
        updateEpic(epic);

        subtaskList.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        List<Integer> idSubtaskForRemove = new ArrayList<>();

        for (Subtask subtask : subtaskList.values()) {
            if (subtask.getEpicId() == id) {
                idSubtaskForRemove.add(subtask.getTaskId());
            }
        }

        for (int idForRemove : idSubtaskForRemove) {
            subtaskList.remove(idForRemove);
            historyManager.remove(idForRemove);
        }
        epicList.remove(id);
        historyManager.remove(id);
    }
}