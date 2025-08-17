package ru.yandex.practicum;

import ru.yandex.practicum.manager.FileBackedTaskManager;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    static void main() throws IOException {

        Path testFile = Files.createTempFile("TEST", ".csv");

        FileBackedTaskManager taskManager = new FileBackedTaskManager(testFile);

        //ПОЛЬЗОВАТЕЛЬСКИЙ СЦЕНАРИЙ РАБОТЫ СО ВРЕМЕНЕМ
        //1. Создаем TASK
        Task task = taskManager.createNewTask("Тестовая задача 1", "Тестовая информация");
        taskManager.setTimeTask(task, LocalDateTime.of(2025, 8, 12, 16, 0),
                Duration.ofMinutes(25));
        // System.out.println(task); //проверяем корректность отображения

        //2. Создаем EPIC с SUBTASK

        Task epic = taskManager.createNewEpic("Тестовая EPIC задача 1", "Тестовая информация");
        int epicId = epic.getTaskId();

        Subtask subtask = taskManager.createNewSubtask("Тестовая подзадача 1", "Тестовая информация",
                epicId);
        taskManager.setTimeTask(subtask, LocalDateTime.of(2025, 8, 13, 10, 0),
                Duration.ofMinutes(30));

        Task subtask2 = taskManager.createNewSubtask("Тестовая подзадача 2", "Тестовая информация",
                epicId);
        taskManager.setTimeTask(subtask2, LocalDateTime.of(2025, 8, 13, 17, 0),
                Duration.ofMinutes(30));

        Task subtask3 = taskManager.createNewSubtask("Тестовая подзадача 3", "Тестовая информация",
                epicId);
        taskManager.setTimeTask(subtask3, LocalDateTime.of(2025, 8, 13, 15, 0),
                Duration.ofMinutes(30));
        //System.out.println(taskManager.getPrioritizedTasks()); // проверяем приоритизацию задач

        //3. Меняем время у SUBTASK
        taskManager.setTimeTask(subtask, LocalDateTime.of(2025, 8, 12, 10, 0),
                Duration.ofMinutes(30));
        //System.out.println(taskManager.getPrioritizedTasks()); // проверяем изменение данных в EPIC и приоритизации

        //4. Меняем статус SUBTASK
        taskManager.updateSubtaskStatus(subtask, TaskStatus.DONE);
        // System.out.println(taskManager.getPrioritizedTasks()); // проверяем изменение данных в EPIC

        //5. Удаляем SUBTASK
        taskManager.removeSubtaskById(subtask.getTaskId());
        //System.out.println(taskManager.getPrioritizedTasks()); // проверяем изменение данных в EPIC и приоритизации

        //6. Удаляем EPIC
        taskManager.removeEpicById(epicId);
        //System.out.println(taskManager.getPrioritizedTasks()); // проверяем удаление EPIC и SUBTASK

        /*7. Вызываем конфликт времени
        Task task2 = taskManager.createNewTask("Тестовая задача 1", "Тестовая информация");
        taskManager.setTimeTask(task2, LocalDateTime.of(2025, 8, 12, 16, 0),
                Duration.ofMinutes(25));
        System.out.println(taskManager.getPrioritizedTasks());*/ // ошибка

        //8. Загрузка файла
        FileBackedTaskManager loadTaskManager = FileBackedTaskManager.loadFromFile(testFile);

        try (BufferedReader reader = Files.newBufferedReader(testFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException exception) {
            throw new IOException("Ошибка чтения файла");
        }

        System.out.println(loadTaskManager.getAllTask());
    }
}