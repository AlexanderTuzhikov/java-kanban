package ru.yandex.practicum.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.testng.Assert.*;

import ru.yandex.practicum.util.Managers;
import ru.yandex.practicum.util.Stubs;

import java.util.List;

class HistoryManagerTest {

    TaskManager taskManagerTest = Managers.getDefault();
    HistoryManager historyManagerTest = Managers.getDefaultHistory();

    @DisplayName("Очищаем историю для чистоты теста")
    @BeforeEach
    void clearHistory() {
        historyManagerTest.getHistory().clear();
    }

    @DisplayName("История работает верно при 5 просмотрах задач")
    @Test
    void test_History_Work_Correctly_If_Get_Task_By_Id_5_Test() {
        //Given
        int TEST_LIST_SIZE = 5;
        List<Task> LIST_TASKS;

        //When
        taskManagerTest.putTask(Stubs.testTask1);

        for (int i = 0; i < 5; i++) {
            taskManagerTest.getTaskById(1);
        }

        LIST_TASKS = historyManagerTest.getHistory();

        //Then
        assertEquals(LIST_TASKS.size(), TEST_LIST_SIZE, "История сохраняется не верно");
    }

    @DisplayName("История работает верно при 10 просмотрах задач")
    @Test
    void test_History_Work_Correctly_If_Get_Task_By_Id_10_Test() {
        //Given
        int TEST_LIST_SIZE = 10;
        List<Task> LIST_TASKS;

        //When
        taskManagerTest.putTask(Stubs.testTask1);

        for (int i = 0; i < 10; i++) {
            taskManagerTest.getTaskById(1);
        }

        LIST_TASKS = historyManagerTest.getHistory();

        //Then
        assertEquals(LIST_TASKS.size(), TEST_LIST_SIZE, "История сохраняется не верно");
    }

    @DisplayName("История работает верно при 15 просмотрах задач")
    @Test
    void test_History_Work_Correctly_If_Get_Task_By_Id_15_Test() {
        //Given
        int TEST_LIST_SIZE = 10;
        List<Task> LIST_TASKS;

        //When
        taskManagerTest.putTask(Stubs.testTask1);

        for (int i = 0; i < 15; i++) {
            taskManagerTest.getTaskById(1);
        }

        LIST_TASKS = historyManagerTest.getHistory();

        //Then
        assertEquals(LIST_TASKS.size(), TEST_LIST_SIZE, "История сохраняется не верно");
    }

    @DisplayName("История сохраняет не ссылки на задачи а сами задачи с их состоянием при просмотре")
    @Test
    void test_History_Work_Correctly_Save_Last_Version_Object_Test() {
        //Given
        taskManagerTest.putTask(Stubs.testTask1);
        List<Task> TEST_HISTORY_LIST = historyManagerTest.getHistory();

        //When
        taskManagerTest.getTaskById(1);
        taskManagerTest.getTaskById(1);

        //Then
        assertEquals(Stubs.testTask1, TEST_HISTORY_LIST.get(0), "Объекты сохраняются не верно");
        assertEquals(Stubs.testTask1, TEST_HISTORY_LIST.get(1), "Объекты сохраняются не верно");

        //When
        Stubs.testTask1.setTaskName("New task Name");
        taskManagerTest.getTaskById(1);

        //Then
        assertNotEquals(TEST_HISTORY_LIST.get(1), TEST_HISTORY_LIST.get(2), "Версии сохраняются некорректно ");
    }

}