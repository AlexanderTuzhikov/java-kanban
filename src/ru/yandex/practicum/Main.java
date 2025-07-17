package ru.yandex.practicum;

import ru.yandex.practicum.tasks.HistoryManager;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskManager;
import ru.yandex.practicum.util.Managers;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        //Реализуем пользовательский сценарий

        //1. Создайте две задачи, эпик с тремя подзадачами и эпик без подзадач.
        Task task = taskManager.createNewTask("Тестовая задача 1", "Тестовая информация");
        int taskId = task.getTaskId();
        Task task2 = taskManager.createNewTask("Тестовая задача 2", "Тестовая информация");
        int taskId2 = task2.getTaskId();

        Task epic = taskManager.createNewEpic("Тестовая EPIC задача 1","Тестовая информация");
        int epicId = epic.getTaskId();
        Task subtask = taskManager.createNewSubtask("Тестовая подзадача 1","Тестовая информация",
                epicId);
        int subtaskId = subtask.getTaskId();
        Task subtask2 = taskManager.createNewSubtask("Тестовая подзадача 2","Тестовая информация",
                epicId);
        int subtaskId2 = subtask2.getTaskId();
        Task subtask3 = taskManager.createNewSubtask("Тестовая подзадача 3","Тестовая информация",
                epicId);
        int subtaskId3 = subtask3.getTaskId();

        Task epic2 = taskManager.createNewEpic("Тестовая EPIC задача 2","Тестовая информация");
        int epicId2 = epic2.getTaskId();

        //2. Запросите созданные задачи несколько раз в разном порядке.
        taskManager.getTaskById(taskId);
        System.out.println(historyManager.getHistory());
        taskManager.getEpicById(epicId);
        System.out.println(historyManager.getHistory());
        taskManager.getTaskById(taskId2);
        System.out.println(historyManager.getHistory());
        taskManager.getSubtaskById(subtaskId);
        taskManager.getSubtaskById(subtaskId2);
        taskManager.getSubtaskById(subtaskId3);
        System.out.println(historyManager.getHistory());
        taskManager.getTaskById(taskId2);
        System.out.println(historyManager.getHistory());

        // 3. Удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться.
        taskManager.removeTaskById(taskId);
        System.out.println(historyManager.getHistory());

        /*4. Удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик,
         так и все его подзадачи.*/
        taskManager.removeEpicById(epicId);
        System.out.println(historyManager.getHistory());

        //СЦЕНАРИЙ ВЫПОЛЯЕТСЯ ПРАВИЛЬНО
    }
}
