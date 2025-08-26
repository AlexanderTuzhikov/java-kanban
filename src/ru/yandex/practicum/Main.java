package ru.yandex.practicum;

import ru.yandex.practicum.manager.impl.FileBackedTaskManager;
import ru.yandex.practicum.tasks.Epic;
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
    public static void main(String[] args) throws IOException {

        Path testFile = Files.createTempFile("TEST", ".csv");

        FileBackedTaskManager taskManager = new FileBackedTaskManager(testFile);

        //ПОЛЬЗОВАТЕЛЬСКИЙ СЦЕНАРИЙ РАБОТЫ СО ВРЕМЕНЕМ
        //1. Создаем TASK
        taskManager.createTask(new Task("Тестовая задача 1", "Тестовая информация", TaskStatus.NEW,
                LocalDateTime.of(2025, 8, 12, 16, 0), Duration.ofMinutes(25)
        ));
        //System.out.println(task); //проверяем корректность отображения

        //2. Создаем EPIC с SUBTASK

        Task epic = taskManager.createEpic(new Epic("Тестовая EPIC задача 1", "Тестовая информация",
                TaskStatus.NEW));
        int epicId = epic.getTaskId();

        Subtask subtask = taskManager.createSubtask(new Subtask("Тестовая подзадача 1", "Тестовая информация", TaskStatus.NEW,
                epicId, LocalDateTime.of(2025, 8, 13, 10, 0), Duration.ofMinutes(30)
        ));


        taskManager.createSubtask(new Subtask("Тестовая подзадача 2", "Тестовая информация", TaskStatus.NEW,
                epicId, LocalDateTime.of(2025, 8, 13, 17, 0), Duration.ofMinutes(30)));
        taskManager.createSubtask(new Subtask("Тестовая подзадача 3", "Тестовая информация", TaskStatus.NEW,
                epicId, LocalDateTime.of(2025, 8, 13, 15, 0), Duration.ofMinutes(30)));
        //System.out.println(taskManager.getPrioritizedTasks()); // проверяем приоритизацию задач

        //3. Меняем время у SUBTASK
        subtask.setStartTime(LocalDateTime.of(2025, 8, 12, 10, 0));
        subtask.setDuration(Duration.ofMinutes(30));
        taskManager.updateSubtask(subtask);
        //System.out.println(taskManager.getPrioritizedTasks()); // проверяем изменение данных в EPIC и приоритизации

        //4. Меняем статус SUBTASK
        subtask.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);
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
        try (BufferedReader reader = Files.newBufferedReader(testFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException exception) {
            throw new IOException("Ошибка чтения файла");
        }

        //System.out.println(loadTaskManager.getAllTask());
    }
}