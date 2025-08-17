package ru.yandex.practicum.manager;

import org.jetbrains.annotations.NotNull;
import ru.yandex.practicum.tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path saveFile;

    public FileBackedTaskManager(Path saveFile) {
        try {
            if (Files.notExists(saveFile)) {
                Files.createFile(saveFile);
                System.out.println("Файл найден! Создан новый файл: " + saveFile);
            } else {
                System.out.println("Файл найден: " + saveFile);
            }
            this.saveFile = saveFile;
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при создании файла: " + saveFile, exception);
        }
    }

    public static @NotNull FileBackedTaskManager loadFromFile(Path saveFile) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(saveFile);
        int loadActualId = 0;

        try (BufferedReader reader = Files.newBufferedReader(saveFile)) {
            Map<Integer, List<Subtask>> subtasksForEpic = new HashMap<>();

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.isBlank() || line.startsWith("id")) {
                    continue;
                }

                Task task = fromString(line)
                        .orElseThrow(() -> new NullPointerException("Передана пустая задача"));
                Type type = task.getType();
                int taskId = task.getTaskId();

                if (taskId > loadActualId) {
                    loadActualId = taskId + 1;
                }

                switch (type) {
                    case TASK -> {
                        fileBackedTaskManager.putTask(task);
                        fileBackedTaskManager.updateTaskTime(task);
                    }
                    case EPIC -> fileBackedTaskManager.putEpic((Epic) task);
                    case SUBTASK -> {
                        Subtask subtask = (Subtask) task;
                        fileBackedTaskManager.putSubtask(subtask);
                        fileBackedTaskManager.updateSubtaskTime((Subtask) task);

                        int epicId = subtask.getEpicId();

                        if (subtasksForEpic.containsKey(epicId)) {
                            List<Subtask> subtasks = subtasksForEpic.get(epicId);
                            subtasks.add(subtask);
                        } else {
                            subtasksForEpic.put(epicId, new ArrayList<>(List.of(subtask)));
                        }
                    }
                }
            }

            updateSubtaskInEpic(fileBackedTaskManager, subtasksForEpic);
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения файла: " + saveFile, exception);
        }

        fileBackedTaskManager.setTaskId(loadActualId);
        return fileBackedTaskManager;
    }

    private static void updateSubtaskInEpic(FileBackedTaskManager fileBackedTaskManager,
                                            Map<Integer, List<Subtask>> subtasksForEpic) {
        for (int epicId : subtasksForEpic.keySet()) {
            Epic epic = fileBackedTaskManager.epicList.get(epicId);
            List<Subtask> subtasks = subtasksForEpic.get(epicId);
            for (Subtask subtaskToAdd : subtasks) {
                int subtaskID = subtaskToAdd.getTaskId();
                epic.setSubtaskForEpic(subtaskID, subtaskToAdd);
            }
        }

        List<Epic> epics = fileBackedTaskManager.getAllEpic();

        for (Epic epic : epics) {
            fileBackedTaskManager.updateEpicStatus(epic);
            fileBackedTaskManager.updateEpicTime(epic);
        }
    }

    private void setTaskId(int id) {
        this.taskId = id;
    }

    private void save() {
        List<Task> tasks = getAllTask();
        List<Epic> epics = getAllEpic();
        List<Subtask> subtasks = getAllSubtaskTask();

        String headString = "id,type,name,status,description,epic,duration,startTime,endTime";

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(saveFile)) {
            bufferedWriter.write(headString);
            bufferedWriter.newLine();

            for (Task task : tasks) {
                String stringTask = task.toCvs();
                bufferedWriter.write(stringTask);
                bufferedWriter.newLine();
            }

            for (Epic epic : epics) {
                String stringEpic = epic.toCvs();
                bufferedWriter.write(stringEpic);
                bufferedWriter.newLine();
            }

            for (Subtask subtask : subtasks) {
                String stringSubtask = subtask.toCvs();
                bufferedWriter.write(stringSubtask);
                bufferedWriter.newLine();
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи файла: " + saveFile, exception);
        }
    }

    public static Optional<Task> fromString(String value) {
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
            case TASK -> Optional.of(new Task(taskId, type, taskName, status, taskInfo, duration, startTime));
            case SUBTASK -> {
                int epicId = Integer.parseInt(strings[5]);
                yield Optional.of(new Subtask(taskId, type, taskName, status, taskInfo, epicId, duration, startTime));
            }
            case EPIC -> {
                LocalDateTime endTime = null;
                if (!strings[8].equals("null") && !strings[8].isBlank()) {
                    endTime = LocalDateTime.parse(strings[8]);
                }
                yield Optional.of(new Epic(taskId, type, taskName, status, taskInfo, duration, startTime, endTime));
            }
        };
    }

    @Override
    public void setTimeTask(Task task, LocalDateTime startTime, Duration duration) {
        super.setTimeTask(task, startTime, duration);
        save();
    }

    @Override
    public Task createNewTask(String taskName, String taskInfo) {
        Task task = super.createNewTask(taskName, taskInfo);
        save();
        return task;
    }

    @Override
    public Subtask createNewSubtask(String taskName, String taskInfo, int epicId) {
        Subtask subtask = super.createNewSubtask(taskName, taskInfo, epicId);
        save();
        return subtask;

    }

    @Override
    public Epic createNewEpic(String epicName, String epicInfo) {
        Epic epic = super.createNewEpic(epicName, epicInfo);
        save();
        return epic;
    }

    @Override
    public void updateTaskStatus(Task task, TaskStatus status) {
        super.updateTaskStatus(task, status);
        save();
    }

    @Override
    public void updateTaskTime(Task task) {
        super.updateTaskTime(task);
        save();
    }

    @Override
    public void updateSubtaskStatus(Subtask subtask, TaskStatus status) {
        super.updateSubtaskStatus(subtask, status);
        save();
    }

    @Override
    public void updateSubtaskTime(Subtask subtask) {
        super.updateSubtaskTime(subtask);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public void updateEpicTime(Epic epic) {
        super.updateEpicTime(epic);
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
