package ru.yandex.practicum.util;

import ru.yandex.practicum.tasks.*;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.testng.Assert.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

class ManagersTest {

    @Test
    void utilClassManagersReturnWorkObjects() {
        TaskManager test = Managers.getDefault();
        assertNotNull(test, "Объект создан пустым!");

        HistoryManager test2 = Managers.getDefaultHistory();
        assertNotNull(test2, "Объект создан пустым!");

        test.createNewTask("Test name","Test info");
        assertNotNull(test.getAllTask(), "Задачи не добавляются");

        test.createNewEpic("Test name", "Test info");
        assertNotNull(test.getAllEpic(), "Epic не добавляются");

        Epic epic = test.getEpicById(2);
        assertEquals(epic.getTaskId(), 2, "Метод получения ID задачи не работает");

        test.createNewSubtask("Test name","Test info", 2);
        assertNotNull(test.getAllSubtaskTask(), "Подзадачи не добавляются");

        test.removeAllTask();
        List<Task> tasks = test.getAllTask();
        assertTrue(tasks.isEmpty(), "Метод удаления всех задач не работает");

        test.removeEpicById(2);
        List<Epic> epics = test.getAllEpic();
        assertTrue(epics.isEmpty(), "Метод удаления Epic по ID не работает");

        List<Subtask> subtasks = test.getAllSubtaskTask();
        assertTrue(subtasks.isEmpty(),"Метод удаления Подзадач при удалении Epic не работает");

        assertNotNull(test2.getHistory(),"История не сохраняется");
    }

}