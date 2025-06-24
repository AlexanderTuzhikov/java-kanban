package ru.yandex.practicum;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.HistoryManager;
import ru.yandex.practicum.tasks.TaskManager;
import ru.yandex.practicum.tasks.TaskStatus;
import ru.yandex.practicum.util.Managers;

public class Main {
    public static void main(String[] args) {
        //Проверяем выводы программы
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        //Создаем задачи

        taskManager.createNewTask("Test name1","Test info1");
        taskManager.createNewTask("Test name2","Test info2");
        taskManager.createNewTask("Test name3","Test info3");

        taskManager.createNewEpic("Test name4", "Test info4");
        taskManager.createNewEpic("Test name5", "Test info5");
        taskManager.createNewEpic("Test name6", "Test info6");

        taskManager.createNewSubtask("Test name7","Test info7",4);
        taskManager.createNewSubtask("Test name8","Test info8",4);
        taskManager.createNewSubtask("Test name9","Test info9",5);

        System.out.println("\t\t\tВсе задачи: \n"  + taskManager.getAllTask());

        System.out.println("\t\t\tВсе Epic с их подзадачами: \n");
        for(Epic epic : taskManager.getAllEpic()) {
            System.out.println("Epic: " + epic);
            System.out.println("Подзадачи: " + taskManager.getAllEpicSubtask(epic));
            System.out.println("-------------------------------------------------");
        }

        //Изменяем статусы
        taskManager.updateTask(taskManager.getTaskById(1), TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(taskManager.getSubtaskById(7), TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(taskManager.getSubtaskById(9), TaskStatus.DONE);

        System.out.println("\t\t\tСтатус изменен: \n" + taskManager.getTaskById(1));
        System.out.println("\t\t\tСтатус изменен: \n" + taskManager.getSubtaskById(7));
        System.out.println("\t\t\tСтатус изменен у Epic: \n" + taskManager.getEpicById(4));
        System.out.println("\t\t\tСтатус изменен: \n" + taskManager.getSubtaskById(9));
        System.out.println("\t\t\tСтатус изменен у Epic: \n" + taskManager.getEpicById(5));

        //Получаем историю последних 10 просмотров

        System.out.println("\t\t\tИстория последних 10 просмотров задач: \n" +
                historyManager.getHistory());

        taskManager.getTaskById(1);
        taskManager.getSubtaskById(8);
        taskManager.getEpicById(6);

        System.out.println("\t\t\tИстория последних 10 просмотров задач: \n" +
                historyManager.getHistory());


    }
}
