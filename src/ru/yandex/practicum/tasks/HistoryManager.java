package ru.yandex.practicum.tasks;

import java.util.ArrayList;

public interface HistoryManager {

    ArrayList<Task> getHistory();

    void updateHistory(Task task);
}
