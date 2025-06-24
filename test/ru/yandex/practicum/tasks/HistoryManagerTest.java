package ru.yandex.practicum.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.testng.Assert.*;

import ru.yandex.practicum.util.Managers;

import java.util.List;

class HistoryManagerTest {


    TaskManager taskManagerTest = Managers.getDefault();
    HistoryManager historyManagerTest = Managers.getDefaultHistory();

    Task testTask1 = new Task(1, "testName1", "TestInfo1");
    Task testTask2 = new Task(1, "testName2", "TestInfo2");
    Task testTask3 = new Task(3, "testName3", "TestInfo3");

    @BeforeEach
    void clearHistory() {
        historyManagerTest.getHistory().clear();
    }

    @Test
    void historyWorkCorrectlyIfGetTaskById5() {
        taskManagerTest.putTask(testTask1);

        for (int i = 0; i < 5; i++) {
            taskManagerTest.getTaskById(1);
        }
        List<Task> tasks = historyManagerTest.getHistory();

        assertEquals(tasks.size(), 5, "История сохраняется не верно");
    }

    @Test
    void historyWorkCorrectlyIfGetTaskById10() {
        taskManagerTest.putTask(testTask2);
        for (int i = 0; i < 10; i++) {
            taskManagerTest.getTaskById(1);
        }
        List<Task> tasks = historyManagerTest.getHistory();

        assertEquals(tasks.size(), 10, "История сохраняется не верно");
    }

    @Test
    void historyWorkCorrectlyIfGetTaskById15() {
        taskManagerTest.putTask(testTask3);

        for (int i = 0; i < 15; i++) {
            taskManagerTest.getTaskById(3);
        }
        List<Task> tasks = historyManagerTest.getHistory();

        assertEquals(tasks.size(), 10, "История сохраняется не верно");
    }

    @Test
    void historyWorkCorrectlySaveLastLVersionObject() {
        taskManagerTest.putTask(testTask1);

        taskManagerTest.getTaskById(1);
        taskManagerTest.getTaskById(1);

        List<Task> tasks = historyManagerTest.getHistory();

        assertEquals(testTask1, tasks.get(0), "Объекты сохраняются не верно");
        assertEquals(testTask1, tasks.get(1), "Объекты сохраняются не верно");

        testTask1.setTaskName("New task Name");
        taskManagerTest.getTaskById(1);

        assertNotEquals(tasks.get(1), tasks.get(2), "Версии сохраняются некорректно ");
    }

}