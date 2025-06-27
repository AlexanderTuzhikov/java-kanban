package ru.yandex.practicum.util;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

public class Stubs {

    public  static Task testTask1 = new Task(1, "testName1", "TestInfo1");
    public  static Task testTask2 = new Task(1, "testName2", "TestInfo2");
    public  static Task testTask3 = new Task(3, "testName3", "TestInfo3");

    public static Epic testEpic1 = new Epic(1, "testName1", "TestInfo1");
    public static Epic testEpic2 = new Epic(1, "testName2", "TestInfo2");
    public static Epic testEpic3 = new Epic(3, "testName3", "TestInfo3");

    public static Subtask testSubtask1 = new Subtask(1, "testName1", "TestInfo1", 1);
    public static Subtask testSubtask2 = new Subtask(1, "testName2", "TestInfo2", 1);
    public static Subtask testSubtask3 = new Subtask(3, "testName3", "TestInfo3", 3);

}
