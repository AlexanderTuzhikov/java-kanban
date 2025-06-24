package ru.yandex.practicum.manager;

import org.junit.jupiter.api.Test;

import static org.testng.Assert.*;

import ru.yandex.practicum.tasks.*;
import ru.yandex.practicum.util.Managers;

class InMemoryTaskManagerTest {

    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void createTaskSubtaskEpic() {
        Task testTask1 = taskManager.createNewTask("Test name", "Test info");
        Epic testEpic1 = taskManager.createNewEpic("Test name", "Test info");
        Subtask testSubtask1 = taskManager.createNewSubtask("Test name", "Test info", testEpic1.getTaskId());
        Subtask testSubtask2 = taskManager.createNewSubtask("Test name", "Test info", 3);
        // Не может быть создан в несуществующий Epic

        assertNotNull(testTask1, "Задача создана некорректно");
        assertNotNull(testSubtask1, "Подзадача создана некорректно");
        assertNotNull(testEpic1, "Epic создан некорректно");
        assertNull(testSubtask2, "Не должна быть создана");
    }
}