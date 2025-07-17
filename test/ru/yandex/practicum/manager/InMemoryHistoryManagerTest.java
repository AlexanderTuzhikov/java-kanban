package ru.yandex.practicum.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.*;
import ru.yandex.practicum.util.Managers;

import java.util.List;

import static org.testng.Assert.assertEquals;

class InMemoryHistoryManagerTest {
    TaskManager taskManagerTest;
    HistoryManager historyManagerTest;

    @DisplayName("Подготовка среды")
    @BeforeEach
    void setUp() {
        taskManagerTest = Managers.getDefault();
        historyManagerTest = Managers.getDefaultHistory();
        taskManagerTest.removeAllTask();
        taskManagerTest.removeAllEpic();
    }

    @DisplayName("При просмотре задачи сохранится в историю")
    @Test
    void test_History_Work_Correctly_If_Get_Task_By_Id() {
        //Given
        final int TEST_LIST_SIZE = 1;
        Task testTask = taskManagerTest.createNewTask("Test Name","Test Info");
        int id = testTask.getTaskId();
        List<Task> testHistory = historyManagerTest.getHistory();

        //When
        taskManagerTest.getTaskById(id);

        //Then
        testHistory = historyManagerTest.getHistory();
        assertEquals(testHistory.size(), 1,"История сохраняет 1 задачу");
        assertEquals(testHistory.get(0), testTask, "Сохранена верная задача");
    }

    @DisplayName("При повторном просмотре задача не дублируется")
    @Test
    void test_History_Work_Correctly_If_Get_Task_By_Id_Again() {
        //Given
        final int TEST_LIST_SIZE = 1;
        Task testTask = taskManagerTest.createNewTask("Test Name","Test Info");
        int id = testTask.getTaskId();

        //When
        taskManagerTest.getTaskById(id);
        taskManagerTest.getTaskById(id);
        taskManagerTest.getTaskById(id);

        //Then
        List<Task> testHistory = historyManagerTest.getHistory();
        assertEquals(testHistory.size(), TEST_LIST_SIZE,"История сохраняется не верно");
    }

    @DisplayName("При удалении задачи она пропадает из просмотров")
    @Test
    void test_Delete_Task_From_Views() {
        //Given
        final int TEST_LIST_SIZE = 0;
        Task testTask = taskManagerTest.createNewTask("Test Name","Test Info");
        int id = testTask.getTaskId();

        //When
        taskManagerTest.getTaskById(id);
        taskManagerTest.removeTaskById(id);

        //Then
        List<Task> testHistory = historyManagerTest.getHistory();
        assertEquals(testHistory.size(), TEST_LIST_SIZE,"История сохраняется не верно");
    }

    @DisplayName("При удалении Epic удаляются и его Subtask из просмотров")
    @Test
    void test_Delete_Epic_From_Views_And_Subtask() {
        //Given
        final int TEST_LIST_SIZE = 0;
        Epic testEpic = taskManagerTest.createNewEpic("Test Epic", "Test Info");
        int idEpic = testEpic.getTaskId();
        Subtask testSubtask = taskManagerTest.createNewSubtask("Test Subtask", "Test Info", idEpic);
        int idSubtask = testSubtask.getTaskId();

        //When
        taskManagerTest.getEpicById(idEpic);
        taskManagerTest.getSubtaskById(idSubtask);
        taskManagerTest.removeEpicById(idEpic);

        //Then
        List<Task> testHistory = historyManagerTest.getHistory();
        assertEquals(testHistory.size(), TEST_LIST_SIZE,"История сохраняется не верно");
    }

    @DisplayName("При повторном просмотре задача переносится вперед списка")
    @Test
    void test_Viewed_Again_Task_Moves_To_The_Tail() {
        //Given
        final int TEST_LIST_SIZE = 2;
        Epic testEpic = taskManagerTest.createNewEpic("Test Epic", "Test Info");
        Epic testEpic2 = taskManagerTest.createNewEpic("Test Epic", "Test Info");
        int idEpic = testEpic.getTaskId();
        int idEpic2 = testEpic2.getTaskId();

        //When
        taskManagerTest.getEpicById(idEpic);
        taskManagerTest.getEpicById(idEpic2);
        taskManagerTest.getEpicById(idEpic);

        //Then
        List<Task> testHistory = historyManagerTest.getHistory();

        assertEquals(testHistory.size(), TEST_LIST_SIZE,"История сохраняется не верно");
        assertEquals(testHistory.get(0), testEpic2, "Порядок просмотров не верен");
        assertEquals(testHistory.get(1), testEpic, "Порядок просмотров не верен");

    }
}