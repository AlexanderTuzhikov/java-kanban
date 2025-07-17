package ru.yandex.practicum.manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.testng.Assert.*;

import ru.yandex.practicum.tasks.*;
import ru.yandex.practicum.util.Managers;

class InMemoryTaskManagerTest {

    TaskManager taskManager = Managers.getDefault();

    @DisplayName("Через утилитарный класс Managers должны корректно создаваться задачи разных типов")
    @Test
    void create_Task_Subtask_Epic() {
        //Given
        Task testTask1;
        Epic testEpic1;
        Subtask testSubtask1;
        Subtask testSubtask2;

        //When
        testTask1 = taskManager.createNewTask("Test name", "Test info");
        testEpic1 = taskManager.createNewEpic("Test name", "Test info");
        testSubtask1 = taskManager.createNewSubtask("Test name", "Test info",
                testEpic1.getTaskId());
        testSubtask2 = taskManager.createNewSubtask("Test name", "Test info", 3);

        //Then
        assertNotNull(testTask1, "Задача создана некорректно");
        assertNotNull(testSubtask1, "Подзадача создана некорректно");
        assertNotNull(testEpic1, "Epic создан некорректно");
        assertNull(testSubtask2, "Не должна быть создана");
    }
}