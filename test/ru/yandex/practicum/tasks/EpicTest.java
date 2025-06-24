package ru.yandex.practicum.tasks;

import org.junit.jupiter.api.Test;

import static org.testng.Assert.*;

class EpicTest {

    Epic testEpic1 = new Epic(1, "testName1", "TestInfo1");
    Epic testEpic2 = new Epic(1, "testName2", "TestInfo2");
    Epic testEpic3 = new Epic(3, "testName3", "TestInfo3");

    @Test
    void epicFieldsAreInitializedCorrectly() {
        assertEquals(testEpic1.getTaskId(), 1,
                "ID задачи не верен");
        assertEquals(testEpic1.getTaskName(), "testName1",
                "Имя задачи не верно");
        assertEquals(testEpic1.getTaskInfo(), "TestInfo1",
                "Информация о задаче не верна");
    }

    @Test
    void setStatusCorrectly() {
        testEpic2.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(testEpic2.getStatus(), TaskStatus.IN_PROGRESS,
                "Установка статуса работает не корректно");
    }

    @Test
    void setNameAndSetInfoCorrectly() {
        testEpic3.setTaskName("New Test Name");
        testEpic3.setTaskInfo("New Test Info");

        assertEquals(testEpic3.getTaskName(),
                "New Test Name", "Установка нового имени работает не корректно");
        assertEquals(testEpic3.getTaskInfo(), "New Test Info",
                "Установка новой информации работает не корректно");
    }

    @Test
    void identicalEpicsByFieldsAreComparedCorrectly() {
        testEpic2.setTaskInfo(testEpic1.getTaskInfo());
        testEpic2.setTaskName(testEpic1.getTaskName());
        testEpic2.setStatus(testEpic1.getStatus());

        assertEquals(testEpic2, testEpic1,
                "Задачи не равны");
    }

    @Test
    void setSubtaskForEpicCorrectly() {
        Subtask testSubtask3 = new Subtask(
                4, "testName3", "TestInfo3", 3);
        testEpic3.setSubtaskForEpic(testSubtask3.getTaskId(), testSubtask3);

        assertNotNull(testEpic3.getSubtaskForEpic(), "Подзадача не добавлена к Epic");
    }

}