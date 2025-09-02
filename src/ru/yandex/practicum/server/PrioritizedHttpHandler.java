package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.manager.HistoryManager;
import ru.yandex.practicum.manager.impl.InMemoryTaskManager;

import java.io.IOException;
import java.util.Objects;

public class PrioritizedHttpHandler extends BaseHttpHandler {

    public PrioritizedHttpHandler(InMemoryTaskManager taskManager, HistoryManager historyManager) {
        super(taskManager, historyManager);
    }

    @Override
    protected void handleGet(HttpExchange httpExchange, String[] splitPath) throws IOException {
        try {
            sendText(httpExchange, gson.toJson(taskManager.getPrioritizedTasks()));
        } catch (RuntimeException exception) {
            sendServerError(httpExchange, "Internal Server Error");
        }
    }

    @Override
    protected void handlePost(HttpExchange httpExchange, String bodyText) throws IOException {
        Objects.requireNonNull(httpExchange, "HttpExchange не может быть null");

        String method = httpExchange.getRequestMethod();
        sendNotFound(httpExchange, "Command " + method + " not found");
    }

    @Override
    protected void handleDelete(HttpExchange httpExchange, String[] splitPath) throws IOException {
        Objects.requireNonNull(httpExchange, "HttpExchange не может быть null");

        String method = httpExchange.getRequestMethod();
        sendNotFound(httpExchange, "Command " + method + " not found");
    }
}
