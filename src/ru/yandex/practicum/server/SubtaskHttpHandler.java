package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.exceptions.TimeConflictException;
import ru.yandex.practicum.manager.HistoryManager;
import ru.yandex.practicum.manager.impl.InMemoryTaskManager;
import ru.yandex.practicum.tasks.Subtask;

import java.io.IOException;
import java.util.Objects;

public class SubtaskHttpHandler extends BaseHttpHandler {

    public SubtaskHttpHandler(InMemoryTaskManager taskManager, HistoryManager historyManager) {
        super(taskManager, historyManager);
    }

    @Override
    protected void handleGet(HttpExchange httpExchange, String[] splitPath) throws IOException {
        Objects.requireNonNull(httpExchange, "HttpExchange не может быть null");
        Objects.requireNonNull(splitPath, "SplitPath не может быть null");

        if (splitPath.length == 3) {
            try {
                int id = Integer.parseInt(splitPath[2]);

                if (taskManager.getSubtaskById(id).isPresent()) {
                    Subtask subtask = taskManager.getSubtaskById(id).get();
                    sendText(httpExchange, gson.toJson(subtask));
                } else {
                    sendNotFound(httpExchange, "Not Found");
                }
            } catch (NumberFormatException exception) {
                sendNotFound(httpExchange, "Некорректный id");
            }
        } else {
            response = gson.toJson(taskManager.getAllSubtask());
            sendText(httpExchange, response);
        }
    }

    @Override
    protected void handlePost(HttpExchange httpExchange, String bodyText) throws IOException {
        Objects.requireNonNull(httpExchange, "HttpExchange не может быть null");

        String contentType = httpExchange.getRequestHeaders().getFirst("Content-Type");

        if (contentType == null || !contentType.contains("application/json")) {
            sendNotFound(httpExchange, "Ожидался Content-Type: application/json");
        }

        Subtask subtask = gson.fromJson(bodyText, Subtask.class);

        if (subtask.getEpicId() == 0 || taskManager.getEpicById(subtask.getEpicId()).isEmpty()) {
            sendNotFound(httpExchange, "Epic с таким id не найден");
            return;
        }

        if (subtask.getTaskId() != 0 && taskManager.getSubtaskById(subtask.getTaskId()).isPresent()) {
            try {
                taskManager.updateSubtask(subtask);
                sendText(httpExchange, "Done");
            } catch (TimeConflictException exception) {
                sendHasOverlaps(httpExchange, "Not Acceptable");
            }
        } else {
            try {
                taskManager.createSubtask(subtask);
                sendText(httpExchange, "Done");
            } catch (TimeConflictException exception) {
                sendHasOverlaps(httpExchange, "Not Acceptable");
            }
        }
    }

    @Override
    protected void handleDelete(HttpExchange httpExchange, String[] splitPath) throws IOException {
        Objects.requireNonNull(httpExchange, "HttpExchange не может быть null");
        Objects.requireNonNull(splitPath, "SplitPath не может быть null");

        try {
            if (splitPath.length == 3) {
                int id = Integer.parseInt(splitPath[2]);
                if (taskManager.getSubtaskById(id).isPresent()) {
                    taskManager.removeSubtaskById(id);
                    sendText(httpExchange, "Done");
                } else {
                    sendNotFound(httpExchange, "Not Found");
                }
            } else {
                sendNotFound(httpExchange, "Не корректный запрос, нужен id");
            }
        } catch (NumberFormatException exception) {
            sendNotFound(httpExchange, "Некорректный id");
        }
    }
}

