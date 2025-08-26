package ru.yandex.practicum.util;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

public class Stubs {

    public static Task testTask1 = new Task("testName1", "TestInfo1", TaskStatus.NEW);
    public static Task testTask2 = new Task("testName2", "TestInfo2", TaskStatus.NEW);
    public static Task testTask3 = new Task("testName3", "TestInfo3", TaskStatus.NEW);

    public static Epic testEpic1 = new Epic("testName1", "TestInfo1", TaskStatus.NEW);
    public static Epic testEpic2 = new Epic("testName2", "TestInfo2", TaskStatus.NEW);
    public static Epic testEpic3 = new Epic("testName3", "TestInfo3", TaskStatus.NEW);

    public static Subtask testSubtask1 = new Subtask("testName1", "TestInfo1", TaskStatus.NEW,1);
    public static Subtask testSubtask2 = new Subtask("testName2", "TestInfo2", TaskStatus.NEW, 1);
    public static Subtask testSubtask3 = new Subtask("testName3", "TestInfo3", TaskStatus.NEW, 3);
    public static Subtask testSubtask4 = new Subtask("testName3", "TestInfo3", TaskStatus.NEW, 1);

}
