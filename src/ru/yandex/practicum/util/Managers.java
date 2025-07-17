package ru.yandex.practicum.util;

import ru.yandex.practicum.manager.InMemoryHistoryManager;
import ru.yandex.practicum.manager.InMemoryTaskManager;
import ru.yandex.practicum.manager.HistoryManager;
import ru.yandex.practicum.manager.TaskManager;

public class Managers {

    private final static InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    private final static InMemoryTaskManager taskManager = new InMemoryTaskManager();

    private Managers() {
    }

    public static TaskManager getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}