package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.*;
import ru.yandex.practicum.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    protected final Map<Integer, Task> taskList = new HashMap<>();
    protected final Map<Integer, Subtask> subtaskList = new HashMap<>();
    protected final Map<Integer, Epic> epicList = new HashMap<>();
    protected final Set<Task> sortTaskByStartTime = new TreeSet<>(Comparator.comparing(Task::getStartTime).
            thenComparing(Task::getTaskId));
    protected final BitSet timeControl = new BitSet();

    protected int taskId = 1;

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortTaskByStartTime);
    }

    private long convertDateTimeToSlot(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "Время не может быть null");
        LocalDateTime baseTime = LocalDateTime.of(2025, 1, 1, 0, 0);

        return ChronoUnit.MINUTES.between(baseTime, localDateTime) / 15;
    }

    private void removeFromTimeControl(Task task) {
        Objects.requireNonNull(task, "Задача не может быть null");

        if (task.getStartTime() == null || task.getEndTime() == null) {
            return;
        }

        long timeControlStart = convertDateTimeToSlot(task.getStartTime());
        long timeControlFinish = convertDateTimeToSlot(task.getEndTime());

        timeControl.clear((int) timeControlStart, (int) timeControlFinish);
    }

    private boolean isTimeConflict(Task task) {
        Objects.requireNonNull(task, "Задача не может быть null");

        if (task.getStartTime() == null || task.getEndTime() == null) {
            return false;
        }

        long timeControlStart = convertDateTimeToSlot(task.getStartTime());
        long timeControlFinish = convertDateTimeToSlot(task.getEndTime());

        boolean isConflict = timeControl.get((int) timeControlStart, (int) timeControlFinish).cardinality() > 0;

        if (!isConflict) {
            timeControl.set((int) timeControlStart, (int) timeControlFinish);
        }

        return isConflict;
    }

    @Override
    public void setTimeTask(Task task, LocalDateTime startTime, Duration duration) {
        Objects.requireNonNull(task, "Задача не может быть null");
        Objects.requireNonNull(startTime, "Время не может быть null");
        Objects.requireNonNull(duration, "Продолжительность не может быть null");

        if (task.getStartTime() != null && task.getDuration() != null) {
            if (!task.getStartTime().isEqual(startTime) || !task.getDuration().equals(duration)) {
                removeFromTimeControl(task);
            }
        }

        task.setStartTime(startTime);
        task.setDuration(duration);

        switch (task.getType()) {
            case TASK -> updateTaskTime(task);
            case SUBTASK -> updateSubtaskTime((Subtask) task);
        }
    }

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
            throw new IllegalArgumentException("Эпик с ID: " + epicId + " не найден");
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
    public ArrayList<Subtask> getAllSubtaskTask() {
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
    public void updateTaskStatus(Task task, TaskStatus status) {
        Objects.requireNonNull(task, "Задача не может быть null");
        Objects.requireNonNull(status, "Статус не может быть null");

        task.setStatus(status);

        taskList.put(task.getTaskId(), task);
    }

    @Override
    public void updateTaskTime(Task task) {
        Objects.requireNonNull(task, "Задача не может быть null");

        if (task.getStartTime() == null || task.getEndTime() == null) {
            taskList.put(task.getTaskId(), task);
            return;
        }

        boolean hasTimeConflict = isTimeConflict(task);

        if (!hasTimeConflict) {
            taskList.put(task.getTaskId(), task);
            sortTaskByStartTime.removeIf(oldTask -> oldTask.getTaskId() == task.getTaskId());
            sortTaskByStartTime.add(task);
        } else {
            throw new TimeConflictException("Обнаружено пересечение времени задач при добавлении:\n " + task);
        }
    }

    @Override
    public void updateSubtaskStatus(Subtask subtask, TaskStatus status) {
        Objects.requireNonNull(subtask, "Задача не может быть null");
        Objects.requireNonNull(status, "Статус не может быть null");

        subtask.setStatus(status);
        subtaskList.put(subtask.getTaskId(), subtask);

        Epic epic = epicList.get(subtask.getEpicId());
        updateEpicStatus(epic);
    }

    @Override
    public void updateSubtaskTime(Subtask subtask) {
        Objects.requireNonNull(subtask, "Задача не может быть null");

        if (subtask.getStartTime() == null || subtask.getEndTime() == null) {
            subtaskList.put(subtask.getTaskId(), subtask);
            return;
        }

        boolean hasTimeConflict = isTimeConflict(subtask);

        if (!hasTimeConflict) {
            subtaskList.put(subtask.getTaskId(), subtask);

            sortTaskByStartTime.remove(subtask);
            sortTaskByStartTime.add(subtask);

            Epic epic = epicList.get(subtask.getEpicId());
            epic.setSubtaskForEpic(subtask.getTaskId(), subtask);
            updateEpicTime(epic);
        } else {
            throw new TimeConflictException("Обнаружено пересечение времени задач при добавлении:\n " + subtask);
        }

    }

    @Override
    public void updateEpicStatus(Epic epic) {
        Objects.requireNonNull(epic, "Задача не может быть null");

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

        epicList.put(epic.getTaskId(), epic);
    }

    @Override
    public void updateEpicTime(Epic epic) {
        Objects.requireNonNull(epic, "Задача не может быть null");

        epic.updateStartAndEndTime();
        epicList.put(epic.getTaskId(), epic);

        if (epic.getStartTime() != null && epic.getEndTime() != null) {
            sortTaskByStartTime.removeIf(oldEpic -> oldEpic.getTaskId() == epic.getTaskId());
            sortTaskByStartTime.add(epic);
        } else {
            sortTaskByStartTime.removeIf(oldEpic -> oldEpic.getTaskId() == epic.getTaskId());
        }

    }

    @Override
    public void removeAllTask() {
        for (int idTask : taskList.keySet()) {
            sortTaskByStartTime.removeIf(task -> task.getTaskId() == idTask);
            getTaskById(idTask).ifPresent(this::removeFromTimeControl);
            historyManager.remove(idTask);

        }
        taskList.clear();
    }

    @Override
    public void removeAllSubtask() {
        for (int idTask : subtaskList.keySet()) {
            sortTaskByStartTime.removeIf(task -> task.getTaskId() == idTask);
            getSubtaskById(idTask).ifPresent(this::removeFromTimeControl);
            historyManager.remove(idTask);
        }

        for (Epic epic : epicList.values()) {
            epic.getSubtaskForEpic().clear();
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }

        subtaskList.clear();
    }

    @Override
    public void removeAllEpic() {
        removeAllSubtask();

        for (int idTask : epicList.keySet()) {
            sortTaskByStartTime.removeIf(task -> task.getTaskId() == idTask);
            getEpicById(idTask).ifPresent(this::removeFromTimeControl);
            historyManager.remove(idTask);
        }

        epicList.clear();

    }

    @Override
    public void removeTaskById(int id) {
        sortTaskByStartTime.removeIf(task -> task.getTaskId() == id);
        getTaskById(id).ifPresent(this::removeFromTimeControl);
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
        updateEpicStatus(epic);
        updateEpicTime(epic);

        sortTaskByStartTime.removeIf(task -> task.getTaskId() == id);
        getSubtaskById(id).ifPresent(this::removeFromTimeControl);
        historyManager.remove(id);
        subtaskList.remove(id);

    }

    @Override
    public void removeEpicById(int id) {
        List<Integer> idSubtaskForRemove = subtaskList.values().stream()
                .filter(subtask -> subtask.getEpicId() == id)
                .map(Task::getTaskId)
                .toList();
        for (int idForRemove : idSubtaskForRemove) {
            removeSubtaskById(idForRemove);
        }

        sortTaskByStartTime.removeIf(task -> task.getTaskId() == id);
        getEpicById(id).ifPresent(this::removeFromTimeControl);
        historyManager.remove(id);
        epicList.remove(id);

    }
}