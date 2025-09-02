package ru.yandex.practicum.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.manager.HistoryManager;

import ru.yandex.practicum.manager.impl.InMemoryTaskManager;
import ru.yandex.practicum.server.adapters.DurationAdapter;
import ru.yandex.practicum.server.adapters.LocalDateTimeAdapter;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;


public abstract class BaseHttpHandler implements HttpHandler {
    protected final InMemoryTaskManager taskManager;
    protected final HistoryManager historyManager;
    protected String response;

    public static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .create();

    public BaseHttpHandler(InMemoryTaskManager taskManager, HistoryManager historyManager) {
        this.taskManager = taskManager;
        this.historyManager = historyManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Objects.requireNonNull(httpExchange, "HttpExchange не может быть null");
        String method = httpExchange.getRequestMethod();
        URI uri = httpExchange.getRequestURI();
        String[] splitPath = httpExchange.getRequestURI().getPath().split("/");
        Headers headers = httpExchange.getRequestHeaders();
        String bodyText = "";

        try (InputStream body = httpExchange.getRequestBody()) {
            bodyText = new String(body.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Body: " + bodyText);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        logRequest(method, uri, headers, bodyText);

        switch (method) {
            case "GET" -> handleGet(httpExchange, splitPath);
            case "POST" -> handlePost(httpExchange, bodyText);
            case "DELETE" -> handleDelete(httpExchange, splitPath);
            default -> sendNotFound(httpExchange, "Command " + method + " not found");
        }
    }

    public void logRequest(String method, URI uri, Headers headers, String bodyText) {
        System.out.println("Request: " + method + " " + uri);
        System.out.println("Headers: " + headers);
        System.out.println("Body: " + bodyText);
    }

    public void sendText(HttpExchange httpExchange, String text) throws IOException {
        Objects.requireNonNull(httpExchange, "Запрос не может быть пустым");
        String respText = text;

        if (respText == null || respText.isBlank()) {
            respText = "Done";
        }

        byte[] resp = respText.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, resp.length);
        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            outputStream.write(resp);
            outputStream.flush();
        }
        System.out.println("Code: 200 " + respText);
    }

    public void sendNotFound(HttpExchange httpExchange, String text) throws IOException {
        Objects.requireNonNull(httpExchange, "Запрос не может быть пустым");
        String respText = text;

        if (respText == null || respText.isBlank()) {
            respText = "NotFound";
        }

        byte[] resp = respText.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(404, resp.length);
        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            outputStream.write(resp);
            outputStream.flush();
        }
        System.out.println("Code: 404 " + text);
    }

    public void sendHasOverlaps(HttpExchange httpExchange, String text) throws IOException {
        Objects.requireNonNull(httpExchange, "Запрос не может быть пустым");
        String respText = text;

        if (respText == null || respText.isBlank()) {
            respText = "Not Acceptable";
        }

        byte[] resp = respText.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(406, resp.length);
        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            outputStream.write(resp);
            outputStream.flush();
        }
        System.out.println("Code: 406 " + text);
    }

    public void sendServerError(HttpExchange httpExchange, String text) throws IOException {
        Objects.requireNonNull(httpExchange, "Запрос не может быть пустым");
        String respText = text;

        if (respText == null || respText.isBlank()) {
            respText = "Internal Server Error";
        }

        byte[] resp = respText.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(500, resp.length);
        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            outputStream.write(resp);
            outputStream.flush();
        }
        System.out.println("Code: 500 " + text);
    }

    protected abstract void handleGet(HttpExchange httpExchange, String[] splitPath) throws IOException;

    protected abstract void handlePost(HttpExchange httpExchange, String bodyText) throws IOException;

    protected abstract void handleDelete(HttpExchange httpExchange, String[] splitPath) throws IOException;
}
