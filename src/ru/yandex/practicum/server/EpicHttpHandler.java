package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.exceptions.TimeConflictException;
import ru.yandex.practicum.manager.HistoryManager;
import ru.yandex.practicum.manager.impl.InMemoryTaskManager;
import ru.yandex.practicum.tasks.Epic;


import java.io.IOException;

public class EpicHttpHandler extends BaseHttpHandler {

    public EpicHttpHandler(InMemoryTaskManager taskManager, HistoryManager historyManager) {
        super(taskManager, historyManager);
    }

    @Override
    protected void handleGet(HttpExchange httpExchange, String[] splitPath) throws IOException {
        if (splitPath.length == 3) {
            try {
                int id = Integer.parseInt(splitPath[2]);

                if (taskManager.getEpicById(id).isPresent()) {
                    Epic epic = taskManager.getEpicById(id).get();
                    sendText(httpExchange, gson.toJson(epic));
                } else {
                    sendNotFound(httpExchange, "Not Found");
                }
            } catch (NumberFormatException exception) {
                sendNotFound(httpExchange, "Некорректный id");
            }
        } else if (splitPath.length == 4 && splitPath[3].equals("subtasks")) {
            try {
                int id = Integer.parseInt(splitPath[2]);

                if (taskManager.getEpicById(id).isPresent()) {
                    Epic epic = taskManager.getEpicById(id).get();
                    sendText(httpExchange, gson.toJson(taskManager.getAllEpicSubtask(epic)));
                } else {
                    sendNotFound(httpExchange, "Not Found");
                }

            } catch (NumberFormatException exception) {
                sendNotFound(httpExchange, "Некорректный id");
            }
        } else {
            response = gson.toJson(taskManager.getAllEpic());
            sendText(httpExchange, response);
        }
    }

    @Override
    protected void handlePost(HttpExchange httpExchange, String bodyText) throws IOException {
        String contentType = httpExchange.getRequestHeaders().getFirst("Content-Type");

        if (contentType == null || !contentType.contains("application/json")) {
            sendNotFound(httpExchange, "Ожидался Content-Type: application/json");
        }

        Epic epic = gson.fromJson(bodyText, Epic.class);
        if (epic.getTaskId() != 0 && taskManager.getEpicById(epic.getTaskId()).isPresent()) {
            try {
                taskManager.updateEpic(epic);
                sendText(httpExchange, "Done");
            } catch (TimeConflictException exception) {
                sendHasOverlaps(httpExchange, "Not Acceptable");
            }
        } else {
            try {
                taskManager.createEpic(epic);
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
                if (taskManager.getEpicById(id).isPresent()) {
                    taskManager.removeEpicById(id);
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

