package ru.yandex.practicum.manager.impl;

import ru.yandex.practicum.exceptions.TimeConflictException;
import ru.yandex.practicum.manager.HistoryManager;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.*;
import ru.yandex.practicum.util.Managers;


import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private HistoryManager historyManager = Managers.getDefaultHistory();

    protected final Map<Integer, Task> taskList = new HashMap<>();
    protected final Map<Integer, Subtask> subtaskList = new HashMap<>();
    protected final Map<Integer, Epic> epicList = new HashMap<>();
    protected final Set<Task> sortTaskByStartTime = new TreeSet<>(Comparator.comparing(Task::getStartTime)
            .thenComparing(Task::getTaskId));
    protected final Map<LocalDateTime, Integer> timeControl = new HashMap<>();
    protected int taskId = 1;

    public InMemoryTaskManager() {
    }

    public InMemoryTaskManager(InMemoryHistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortTaskByStartTime);
    }

    public boolean isTimeConflict(Task task) {
        Objects.requireNonNull(task, "Задача не может быть null");
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();

        for (LocalDateTime time = start; time.isBefore(end); time = time.plusMinutes(1)) {
            Integer taskIdAtSlot = timeControl.get(time);
            if (taskIdAtSlot != null && !taskIdAtSlot.equals(task.getTaskId())) {
                System.out.println("Время занято задачей: ID " + taskIdAtSlot);
                return true;
            }
        }

        for (LocalDateTime time = start; time.isBefore(end); time = time.plusMinutes(1)) {
            timeControl.put(time, task.getTaskId());
        }

        return false;
    }

    public void removeFromTimeConflict(Task task) {
        Objects.requireNonNull(task, "Задача не может быть null");
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();

        for (LocalDateTime time = start; time.isBefore(end); time = time.plusMinutes(1)) {
            timeControl.remove(time);
        }
    }

    @Override
    public Task createTask(Task task) {
        Objects.requireNonNull(task, "Задача не может быть null");

        task.setType(Type.TASK);
        task.setTaskId(taskId);

        if (task.getStartTime() != null && task.getDuration() != null) {
            boolean hasTimeConflict = isTimeConflict(task);

            if (!hasTimeConflict) {
                timeControl.put(task.getStartTime(), task.getTaskId());
                sortTaskByStartTime.add(task);
            } else {
                throw new TimeConflictException("Обнаружено пересечение времени задач при добавлении:\n " + task);
            }
        }

        taskId++;
        putTask(task);
        return task;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Objects.requireNonNull(subtask, "Задача не может быть null");

        if (!epicList.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Эпик с ID: " + subtask.getEpicId() + " не найден");
        }
        subtask.setType(Type.SUBTASK);
        subtask.setTaskId(taskId);

        if (subtask.getStartTime() != null && subtask.getDuration() != null) {
            boolean hasTimeConflict = isTimeConflict(subtask);
            if (!hasTimeConflict) {
                timeControl.put(subtask.getStartTime(), subtask.getTaskId());
                sortTaskByStartTime.add(subtask);
            } else {
                throw new TimeConflictException("Обнаружено пересечение времени задач при добавлении:\n " + subtask);
            }
        }

        taskId++;
        putSubtask(subtask);
        Epic epic = epicList.get(subtask.getEpicId());
        epic.setSubtaskForEpic(subtask.getTaskId(), subtask);
        updateEpic(epic);

        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Objects.requireNonNull(epic, "Epic не может быть null");
        epic.setStartTime(null);
        epic.setEndTime(null);
        epic.setDuration(null);
        epic.setType(Type.EPIC);
        epic.setTaskId(taskId);

        taskId++;
        putEpic(epic);
        return epic;
    }

    @Override
    public void putTask(Task task) {
        Objects.requireNonNull(task, "Задача не может быть null");
        int id = task.getTaskId();
        taskList.put(id, task);
    }

    @Override
    public void putSubtask(Subtask subtask) {
        Objects.requireNonNull(subtask, "Задача не может быть null");
        int id = subtask.getTaskId();
        subtaskList.put(id, subtask);
    }

    @Override
    public void putEpic(Epic epic) {
        Objects.requireNonNull(epic, "Задача не может быть null");
        int id = epic.getTaskId();
        epicList.put(id, epic);
    }

    @Override
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(taskList.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtaskList.values());
    }

    @Override
    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epicList.values());
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        Optional<Task> task = Optional.ofNullable(taskList.get(id));
        task.ifPresent(historyManager::add);

        return task;
    }

    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        Optional<Subtask> subtask = Optional.ofNullable(subtaskList.get(id));
        subtask.ifPresent(historyManager::add);

        return subtask;
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        Optional<Epic> epic = Optional.ofNullable(epicList.get(id));
        epic.ifPresent(historyManager::add);

        return epic;
    }

    @Override
    public HashMap<Integer, Subtask> getAllEpicSubtask(Epic epic) {
        Objects.requireNonNull(epic, "Задача не может быть null");

        return epic.getSubtaskForEpic();
    }

    @Override
    public void updateTask(Task task) {
        Objects.requireNonNull(task, "Задача не может быть null");

        taskList.put(task.getTaskId(), task);

        if (task.getStartTime() != null && task.getDuration() != null) {
            boolean hasTimeConflict = isTimeConflict(task);

            if (!hasTimeConflict) {
                timeControl.put(task.getStartTime(), task.getTaskId());
                sortTaskByStartTime.removeIf(oldTask -> oldTask.getTaskId() == task.getTaskId());
                sortTaskByStartTime.add(task);
            } else {
                throw new TimeConflictException("Обнаружено пересечение времени задач при добавлении:\n " + task);
            }
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Objects.requireNonNull(subtask, "Задача не может быть null");

        subtaskList.put(subtask.getTaskId(), subtask);

        if (subtask.getStartTime() != null && subtask.getDuration() != null) {
            boolean hasTimeConflict = isTimeConflict(subtask);

            if (!hasTimeConflict) {
                timeControl.put(subtask.getStartTime(), subtask.getTaskId());
                sortTaskByStartTime.removeIf(oldTask -> oldTask.getTaskId() == subtask.getTaskId());
                sortTaskByStartTime.add(subtask);
            } else {
                throw new TimeConflictException("Обнаружено пересечение времени задач при добавлении:\n " + subtask);
            }
        }

        Epic epic = epicList.get(subtask.getEpicId());
        epic.setSubtaskForEpic(subtask.getTaskId(), subtask);
        updateEpic(epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        Objects.requireNonNull(epic, "Задача не может быть null");
        int statusDone = 0;
        int statusInProgress = 0;
        Map<Integer, Subtask> actualSubtask = getAllEpicSubtask(epic);

        if (actualSubtask.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        }

        for (Subtask subtask : actualSubtask.values()) {
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                statusInProgress++;
            } else if (subtask.getStatus() == TaskStatus.DONE) {
                statusDone++;
            }
        }

        if (statusDone != 0 && statusDone == actualSubtask.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else if (statusInProgress > 0 || statusDone > 0) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }

        epic.updateStartAndEndTime();

        if (epic.getStartTime() != null && epic.getEndTime() != null) {
            sortTaskByStartTime.removeIf(oldEpic -> oldEpic.getTaskId() == epic.getTaskId());
            sortTaskByStartTime.add(epic);
        } else {
            sortTaskByStartTime.removeIf(oldEpic -> oldEpic.getTaskId() == epic.getTaskId());
        }

        epicList.put(epic.getTaskId(), epic);
    }

    @Override
    public void removeAllTask() {
        for (int idTask : taskList.keySet()) {
            Task taskToRemove = taskList.get(idTask);

            if (taskToRemove.getStartTime() != null) {
                removeFromTimeConflict(taskToRemove);
            }

            sortTaskByStartTime.removeIf(task -> task.getTaskId() == idTask);
            historyManager.remove(idTask);
        }

        taskList.clear();
    }

    @Override
    public void removeAllSubtask() {
        for (int idTask : subtaskList.keySet()) {
            Subtask subtask = subtaskList.get(idTask);

            if (subtask.getStartTime() != null) {
                removeFromTimeConflict(subtask);
            }

            sortTaskByStartTime.removeIf(task -> task.getTaskId() == idTask);
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
        removeAllSubtask();

        for (int idTask : epicList.keySet()) {
            sortTaskByStartTime.removeIf(task -> task.getTaskId() == idTask);
            historyManager.remove(idTask);
        }

        epicList.clear();
    }

    @Override
    public void removeTaskById(int id) {
        Task taskToRemove = taskList.get(id);

        if (taskToRemove.getStartTime() != null) {
            removeFromTimeConflict(taskToRemove);
        }

        sortTaskByStartTime.removeIf(task -> task.getTaskId() == id);
        historyManager.remove(id);
        taskList.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtaskList.get(id);
        int epicId = subtask.getEpicId();
        Epic epic = epicList.get(epicId);
        Map<Integer, Subtask> subtasks = epic.getSubtaskForEpic();

        subtasks.remove(id);
        updateEpic(epic);

        if (subtask.getStartTime() != null) {
            removeFromTimeConflict(subtask);
        }

        sortTaskByStartTime.removeIf(task -> task.getTaskId() == id);
        historyManager.remove(id);
        subtaskList.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epicList.get(id);

        for (Integer subtaskId : new ArrayList<>(epic.getSubtaskForEpic().keySet())) {
            removeSubtaskById(subtaskId);
        }

        sortTaskByStartTime.removeIf(task -> task.getTaskId() == id);
        historyManager.remove(id);
        epicList.remove(id);
    }
}