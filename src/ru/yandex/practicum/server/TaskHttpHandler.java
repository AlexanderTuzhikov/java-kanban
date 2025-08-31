package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.exceptions.TimeConflictException;

import ru.yandex.practicum.manager.HistoryManager;
import ru.yandex.practicum.manager.impl.InMemoryTaskManager;
import ru.yandex.practicum.tasks.Task;

import java.io.IOException;

public class TaskHttpHandler extends BaseHttpHandler {

    public TaskHttpHandler(InMemoryTaskManager taskManager, HistoryManager historyManager) {
        super(taskManager, historyManager);
    }

    @Override
    protected void handleGet(HttpExchange httpExchange, String[] splitPath) throws IOException {
        if (splitPath.length == 3) {
            try {
                int id = Integer.parseInt(splitPath[2]);

                if (taskManager.getTaskById(id).isPresent()) {
                    Task task = taskManager.getTaskById(id).get();
                    sendText(httpExchange, gson.toJson(task));
                } else {
                    sendNotFound(httpExchange, "Not Found");
                }
            } catch (NumberFormatException exception) {
                sendNotFound(httpExchange, "Некорректный id");
            }
        } else {
            response = gson.toJson(taskManager.getAllTask());
            sendText(httpExchange, response);
        }
    }

    @Override
    protected void handlePost(HttpExchange httpExchange, String bodyText) throws IOException {
        String contentType = httpExchange.getRequestHeaders().getFirst("Content-Type");

        if (contentType == null || !contentType.contains("application/json")) {
            sendNotFound(httpExchange, "Ожидался Content-Type: application/json");
        }

        Task task = gson.fromJson(bodyText, Task.class);
        if (task.getTaskId() != 0 && taskManager.getTaskById(task.getTaskId()).isPresent()) {
            try {
                taskManager.updateTask(task);
                sendText(httpExchange, "Done");
            } catch (TimeConflictException exception) {
                sendHasOverlaps(httpExchange, "Not Acceptable");
            }
        } else {
            try {
                taskManager.createTask(task);
                sendText(httpExchange, "Done");
            } catch (TimeConflictException exception) {
                sendHasOverlaps(httpExchange, "Not Acceptable");
            }
        }
    }

    @Override
    protected void handleDelete(HttpExchange httpExchange, String[] splitPath) throws IOException {
        try {
            if (splitPath.length == 3) {
                int id = Integer.parseInt(splitPath[2]);
                if (taskManager.getTaskById(id).isPresent()) {
                    taskManager.removeTaskById(id);
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


