package ru.yandex.practicum.util;

import org.junit.jupiter.api.DisplayName;
import ru.yandex.practicum.tasks.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.testng.Assert.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

class ManagersTest {

    @DisplayName("Утилитарный класс возвращает объекты с инициализированными полями  и рабочими методами")
    @Test
    void test_Util_Class_Managers_Return_Work_Objects_Test() {
        //Given
        TaskManager TEST_MANAGER;
        HistoryManager TEST_HISTORY_MANAGER;
        List<Task> TEST_LIST_TASKS;
        List<Epic> TEST_LIST_EPIC;
        List<Subtask> TEST_LIST_SUBTASK;

        //When
        TEST_MANAGER = Managers.getDefault();
        TEST_HISTORY_MANAGER = Managers.getDefaultHistory();

        //Then
        assertNotNull(TEST_MANAGER, "Объект создан пустым!");
        assertNotNull(TEST_HISTORY_MANAGER, "Объект создан пустым!");

        //When
        TEST_MANAGER.createNewTask("TEST_MANAGER name","TEST_MANAGER info");

        //Then
        assertNotNull(TEST_MANAGER.getAllTask(), "Задачи не добавляются");

        //When
        TEST_MANAGER.createNewEpic("TEST_MANAGER name", "TEST_MANAGER info");

        //Then
        assertNotNull(TEST_MANAGER.getAllEpic(), "Epic не добавляются");

        //When
        Epic epic = TEST_MANAGER.getEpicById(2);

        //Then
        assertEquals(epic.getTaskId(), 2, "Метод получения ID задачи не работает");

        //When
        TEST_MANAGER.createNewSubtask("TEST_MANAGER name","TEST_MANAGER info", 2);

        //Then
        assertNotNull(TEST_MANAGER.getAllSubtaskTask(), "Подзадачи не добавляются");

        //When
        TEST_MANAGER.removeAllTask();
        TEST_LIST_TASKS = TEST_MANAGER.getAllTask();

        //Then
        assertTrue(TEST_LIST_TASKS.isEmpty(), "Метод удаления всех задач не работает");

        //When
        TEST_MANAGER.removeEpicById(2);
        TEST_LIST_EPIC = TEST_MANAGER.getAllEpic();

        //Then
        assertTrue(TEST_LIST_EPIC.isEmpty(), "Метод удаления Epic по ID не работает");

        //When
        TEST_LIST_SUBTASK = TEST_MANAGER.getAllSubtaskTask();
        assertTrue(TEST_LIST_SUBTASK.isEmpty(),"Метод удаления Подзадач при удалении Epic не работает");

        //Then
        assertNotNull(TEST_HISTORY_MANAGER.getHistory(),"История не сохраняется");
    }

}