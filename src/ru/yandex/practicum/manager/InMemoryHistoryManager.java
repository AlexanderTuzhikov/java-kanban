package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.HistoryManager;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history = new ArrayList<>();
    private static final int MAX_SIZE_HISTORY = 10;

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }

    @Override
    public void updateHistory(Task task) {
        if(MAX_SIZE_HISTORY == history.size()) {
            history.removeFirst();
            history.add(task);
        } else {
            history.add(task);
        }
    }
}