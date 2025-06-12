package ru.yandex.practicum;

import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        taskManager.createNewTask("Простая задача №1",
                "Дополнительная информация задачи №1");
        taskManager.createNewTask("Простая задача №2",
                "Дополнительная информация задачи №2");
        taskManager.createNewEpic("Epic задача №1",
                "Дополнительная информация epic задачи №1");
        taskManager.createNewSubtask("Подзадача №1 для epic №1",
                "Дополнительная информация подзадачи №1", 3);
        taskManager.createNewSubtask("Подзадача №2 для epic №1",
                "Дополнительная информация подзадачи №2", 3);
        taskManager.createNewEpic("Epic задача №2",
                "Дополнительная информация epic задачи №2");
        taskManager.createNewSubtask("Подзадача №1 для epic №2",
                "Дополнительная информация подзадачи №1", 6);

        System.out.println("\nСписок всех простых задач: \n" + taskManager.getAllTask());
        System.out.println("\nСписок всех epic задач: \n" + taskManager.getAllEpic());
        System.out.println("\nСписок всех подзадач: \n" + taskManager.getAllSubtaskTask());

        taskManager.updateTask(taskManager.getTaskById(1), TaskStatus.DONE);
        System.out.println("\nИзменен статус простой задачи №1 - DONE\n" + taskManager.getTaskById(1));
        taskManager.updateTask(taskManager.getTaskById(2), TaskStatus.IN_PROGRESS);
        System.out.println("\nИзменен статус простой задачи №1 - IN_PROGRESS\n" + taskManager.getTaskById(2));
        taskManager.updateSubtask(taskManager.getSubtaskById(4), TaskStatus.IN_PROGRESS);
        System.out.println("\nИзменен статус подзадачи №1 для epic №1 - IN_PROGRESS\n" + taskManager.getSubtaskById(4)
                + "Статус epic №1 изменился:\n" + taskManager.getEpicById(3));

        taskManager.removeTaskById(1);
        taskManager.removeEpicById(3);
        taskManager.updateSubtask(taskManager.getSubtaskById(7), TaskStatus.DONE);
        System.out.println("Удалены: простая задача №1 и epic задача №1 " +
                "\nИзменен статус подзадачи №1 для epic №2 - DONE");
        System.out.println("\nСписок всех простых задач: \n" + taskManager.getAllTask());
        System.out.println("\nСписок всех epic задач: \n" + taskManager.getAllEpic());
        System.out.println("\nСписок всех подзадач: \n" + taskManager.getAllSubtaskTask());
        taskManager.removeAllTask();
        System.out.println("Удалены все задачи: " + taskManager.getAllTask());
        taskManager.removeSubtaskById(7);
        System.out.println("Удалена Подзадача №1 для epic №2 по ID: " + taskManager.getAllSubtaskTask()
                + "\n" + taskManager.getAllEpic());
        taskManager.removeAllEpic();
        System.out.println("Удалены все epic задачи: " + taskManager.getAllEpic());
    }
}
