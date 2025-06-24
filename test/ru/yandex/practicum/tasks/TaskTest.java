package ru.yandex.practicum.tasks;

import org.junit.jupiter.api.Test;

import static org.testng.Assert.*;

class TaskTest {

    Task testTask1 = new Task(1, "testName1", "TestInfo1");
    Task testTask2 = new Task(1, "testName2", "TestInfo2");
    Task testTask3 = new Task(3, "testName3", "TestInfo3");

    @Test
    void taskFieldsAreInitializedCorrectly() {
        assertEquals(testTask1.getTaskId(), 1,
                "ID задачи не верен");
        assertEquals(testTask1.getTaskName(), "testName1",
                "Имя задачи не верно");
        assertEquals(testTask1.getTaskInfo(), "TestInfo1",
                "Информация о задаче не верна");
    }

    @Test
    void setStatusCorrectly() {
        testTask2.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(testTask2.getStatus(), TaskStatus.IN_PROGRESS,
                "Установка статуса работает не корректно");
    }

    @Test
    void setNameAndSetInfoCorrectly() {
        testTask3.setTaskName("New Test Name");
        testTask3.setTaskInfo("New Test Info");

        assertEquals(testTask3.getTaskName(),
                "New Test Name", "Установка нового имени работает не корректно");
        assertEquals(testTask3.getTaskInfo(), "New Test Info",
                "Установка новой информации работает не корректно");
    }

    @Test
    void identicalTasksByFieldsAreComparedCorrectly() {
        testTask2.setTaskInfo(testTask1.getTaskInfo());
        testTask2.setTaskName(testTask1.getTaskName());
        testTask2.setStatus(testTask1.getStatus());

        assertEquals(testTask2, testTask1,
                "Задачи не равны");
    }
}