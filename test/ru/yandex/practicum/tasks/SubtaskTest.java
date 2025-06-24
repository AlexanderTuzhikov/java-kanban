package ru.yandex.practicum.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    Subtask testSubtask1 = new Subtask(1, "testName1", "TestInfo1", 1);
    Subtask testSubtask2 = new Subtask(1, "testName2", "TestInfo2", 1);
    Subtask testSubtask3 = new Subtask(3, "testName3", "TestInfo3", 3);

    @Test
    void SubtaskFieldsAreInitializedCorrectly() {
        assertEquals(1, testSubtask1.getTaskId(),
                "ID задачи не верен");
        assertEquals("testName1", testSubtask1.getTaskName(),
                "Имя задачи не верно");
        assertEquals("TestInfo1", testSubtask1.getTaskInfo(),
                "Информация о задаче не верна");
    }

    @Test
    void setStatusCorrectly() {
        testSubtask2.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, testSubtask2.getStatus(),
                "Установка статуса работает не корректно");
    }

    @Test
    void setNameAndSetInfoCorrectly() {
        testSubtask3.setTaskName("New Test Name");
        testSubtask3.setTaskInfo("New Test Info");

        assertEquals("New Test Name",
                testSubtask3.getTaskName(), "Установка нового имени работает не корректно");
        assertEquals("New Test Info", testSubtask3.getTaskInfo(),
                "Установка новой информации работает не корректно");
    }

    @Test
    void identicalSubtasksByFieldsAreComparedCorrectly() {
        testSubtask2.setTaskInfo(testSubtask1.getTaskInfo());
        testSubtask2.setTaskName(testSubtask1.getTaskName());
        testSubtask2.setStatus(testSubtask1.getStatus());

        assertEquals(testSubtask2, testSubtask1,
                "Задачи не равны");
    }

    @Test
    void getEpicIdCorrectly() {
        assertEquals(3, testSubtask3.getEpicId(), "Метод вызова EpicID работает не корректно ");
    }
}
