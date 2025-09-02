package ru.yandex.practicum.manager.impl;

import ru.yandex.practicum.exceptions.ManagerSaveException;
import ru.yandex.practicum.tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path saveFile;
    private int loadActualId = 1;
    private final Map<Integer, List<Subtask>> subtasksForEpic = new HashMap<>();

    public FileBackedTaskManager(Path saveFile) {
        try {
            if (Files.notExists(saveFile)) {
                Files.createFile(saveFile);
            }
            this.saveFile = saveFile;
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при создании файла: " + saveFile, exception);
        }
    }

    public FileBackedTaskManager loadFromFile(Path saveFile) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(saveFile);

        try (BufferedReader reader = Files.newBufferedReader(saveFile)) {
            String line;

            while ((line = reader.readLine()) != null) {

                if (line.isBlank() || line.startsWith("id")) {
                    continue;
                }

                Task task = fromString(line);
                updateLoadId(task);

                switch (task.getType()) {
                    case TASK -> loadTask(fileBackedTaskManager, task);
                    case EPIC -> loadEpic(fileBackedTaskManager, task);
                    case SUBTASK -> loadSubtask(fileBackedTaskManager, task);
                }
            }

            updateSubtaskFromEpic(fileBackedTaskManager);

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения файла: " + saveFile, exception);
        }

        fileBackedTaskManager.setTaskId(loadActualId);
        return fileBackedTaskManager;
    }

    private void setTaskId(int id) {
        this.taskId = id;
    }

    private void loadTask(FileBackedTaskManager fileBackedTaskManager, Task task) {
        Objects.requireNonNull(fileBackedTaskManager, "Передан пустой менеджер");
        Objects.requireNonNull(task, "Передана пустая задача ");

        fileBackedTaskManager.putTask(task);
        if (task.getStartTime() != null && task.getDuration() != null) {
            fileBackedTaskManager.isTimeConflict(task);
            fileBackedTaskManager.sortTaskByStartTime.add(task);
        }
    }

    private void loadSubtask(FileBackedTaskManager fileBackedTaskManager, Task task) {
        Objects.requireNonNull(fileBackedTaskManager, "Передан пустой менеджер");
        Objects.requireNonNull(task, "Передана пустая задача ");

        Subtask subtask = (Subtask) task;

        subtasksForEpic
                .computeIfAbsent(subtask.getEpicId(), k -> new ArrayList<>())
                .add(subtask);

        fileBackedTaskManager.putSubtask((Subtask) task);

        if (subtask.getStartTime() != null && subtask.getDuration() != null) {
            fileBackedTaskManager.isTimeConflict(subtask);
            fileBackedTaskManager.sortTaskByStartTime.add(subtask);
        }
    }

    private void loadEpic(FileBackedTaskManager fileBackedTaskManager, Task task) {
        Objects.requireNonNull(fileBackedTaskManager, "Передан пустой менеджер");
        Objects.requireNonNull(task, "Передана пустая задача ");

        fileBackedTaskManager.putEpic((Epic) task);

    }

    private void updateLoadId(Task task) {
        Objects.requireNonNull(task, "Передана пустая задача ");

        int taskId = task.getTaskId();

        if (taskId >= loadActualId) {
            loadActualId = taskId + 1;
        }
    }

    private void updateSubtaskFromEpic(FileBackedTaskManager fileBackedTaskManager) {
        for (int epicId : subtasksForEpic.keySet()) {
            Epic epic = fileBackedTaskManager.epicList.get(epicId);
            if (epic == null) continue;
            List<Subtask> subtasks = subtasksForEpic.get(epicId);

            for (Subtask subtaskToAdd : subtasks) {
                int subtaskID = subtaskToAdd.getTaskId();
                epic.setSubtaskForEpic(subtaskID, subtaskToAdd);
            }
            fileBackedTaskManager.updateEpic(epic);
        }
    }

    private void save() {
        List<Task> tasks = getAllTask();
        List<Epic> epics = getAllEpic();
        List<Subtask> subtasks = getAllSubtask();

        String headString = "id,type,name,status,description,epic,duration,startTime,endTime";

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(saveFile)) {
            bufferedWriter.write(headString);
            bufferedWriter.newLine();

            for (Task task : tasks) {
                String stringTask = task.toCsv();
                bufferedWriter.write(stringTask);
                bufferedWriter.newLine();
            }

            for (Epic epic : epics) {
                String stringEpic = epic.toCsv();
                bufferedWriter.write(stringEpic);
                bufferedWriter.newLine();
            }

            for (Subtask subtask : subtasks) {
                String stringSubtask = subtask.toCsv();
                bufferedWriter.write(stringSubtask);
                bufferedWriter.newLine();
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи файла: " + saveFile, exception);
        }
    }

    private Task fromString(String value) {
        Objects.requireNonNull(value, "Передана пустая строка");

        String[] strings = value.split(",");

        int taskId = Integer.parseInt(strings[0]);
        Type type = Type.valueOf(strings[1]);
        String taskName = strings[2];
        TaskStatus status = TaskStatus.valueOf(strings[3]);
        String taskInfo = strings[4];
        Duration duration = null;
        if (!strings[6].equals("null") && !strings[6].isBlank()) {
            duration = Duration.parse(strings[6]);
        }
        LocalDateTime startTime = null;
        if (!strings[7].equals("null") && !strings[7].isBlank()) {
            startTime = LocalDateTime.parse(strings[7]);
        }

        return switch (type) {
            case TASK -> {
                Task task = new Task(taskName, taskInfo, status, startTime, duration);
                task.setTaskId(taskId);
                yield task;
            }

            case SUBTASK -> {
                int epicId = Integer.parseInt(strings[5]);
                Subtask subtask = new Subtask(taskName, taskInfo, status, epicId, startTime, duration);
                subtask.setTaskId(taskId);
                yield subtask;
            }
            case EPIC -> {
                Epic epic = new Epic(taskName, taskInfo, status);
                epic.setTaskId(taskId);
                yield epic;
            }
        };
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }


    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }


    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeAllTask() {
        super.removeAllTask();
        save();
    }

    @Override
    public void removeAllSubtask() {
        super.removeAllSubtask();
        save();
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }
}