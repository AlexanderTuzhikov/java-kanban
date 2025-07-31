package ru.yandex.practicum.manager;
import ru.yandex.practicum.tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static FileBackedTaskManager loadFromFile(Path saveFile) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(saveFile);
        int loadActualID = 0;

        try (BufferedReader reader = Files.newBufferedReader(saveFile)) {
            Map<Integer, List<Subtask>> subtasksForEpic = new HashMap<>();

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.isBlank() || line.startsWith("id")) {
                    continue;
                }

                Task task = fromString(line);
                Type type = task.getType();
                int taskID = task.getTaskId();

                if (taskID > loadActualID) {
                    loadActualID = taskID + 1;
                }

                switch (type) {
                    case TASK -> fileBackedTaskManager.putTask(task);
                    case EPIC -> fileBackedTaskManager.putEpic((Epic) task);
                    case SUBTASK -> {
                        Subtask subtask = (Subtask) task;
                        fileBackedTaskManager.putSubtask(subtask);
                        int epicID = subtask.getEpicId();

                        if (subtasksForEpic.containsKey(epicID)) {
                            List<Subtask> subtasks = subtasksForEpic.get(epicID);
                            subtasks.add(subtask);
                        } else {
                            subtasksForEpic.put(epicID, new ArrayList<>(List.of(subtask)));
                        }
                    }
                }
            }
            for (int EpicID : subtasksForEpic.keySet()) {
                Epic epic = fileBackedTaskManager.epicList.get(EpicID);
                List<Subtask> subtasks = subtasksForEpic.get(EpicID);
                for (Subtask subtaskToAdd : subtasks) {
                    int subtaskID = subtaskToAdd.getTaskId();
                    epic.setSubtaskForEpic(subtaskID, subtaskToAdd);
                }
            }
        } catch (
                IOException exception) {
            throw new ManagerSaveException("Ошибка чтения файла: " + saveFile, exception);
        }

        fileBackedTaskManager.setTaskId(loadActualID);
        return fileBackedTaskManager;
    }

    public void setTaskId(int id) {
        this.taskId = id;
    }

    public void save() {
        List<Task> tasks = getAllTask();
        List<Epic> epics = getAllEpic();
        List<Subtask> subtasks = getAllSubtaskTask();

        String headString = "id,type,name,status,description,epic";

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(saveFile)) {
            bufferedWriter.write(headString);
            bufferedWriter.newLine();

            for (Task task : tasks) {
                String stringTask = task.toString();
                bufferedWriter.write(stringTask);
                bufferedWriter.newLine();
            }

            for (Epic epic : epics) {
                String stringEpic = epic.toString();
                bufferedWriter.write(stringEpic);
                bufferedWriter.newLine();
            }

            for (Subtask subtask : subtasks) {
                String stringSubtask = subtask.toString();
                bufferedWriter.write(stringSubtask);
                bufferedWriter.newLine();
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи файла: " + saveFile, exception);
        }
    }

    public static Task fromString(String value) {
        String[] strings = value.split(",");

        int taskId = Integer.parseInt(strings[0]);
        Type type = Type.valueOf(strings[1]);
        String taskName = strings[2];
        TaskStatus status = TaskStatus.valueOf(strings[3]);
        String taskInfo = strings[4];

        return switch (type) {
            case SUBTASK -> {
                int epicId = Integer.parseInt(strings[5]);
                yield new Subtask(taskId, type, taskName, status, taskInfo, epicId);
            }
            case TASK -> new Task(taskId, type, taskName, status, taskInfo);
            case EPIC -> new Epic(taskId, type, taskName, status, taskInfo);
        };
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
    public void updateTask(Task task, TaskStatus status) {
        super.updateTask(task, status);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask, TaskStatus status) {
        super.updateSubtask(subtask, status);
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
