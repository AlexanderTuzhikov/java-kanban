package ru.yandex.practicum.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.manager.HistoryManager;

import ru.yandex.practicum.manager.impl.InMemoryTaskManager;
import ru.yandex.practicum.server.adapters.DurationAdapter;
import ru.yandex.practicum.server.adapters.LocalDateTimeAdapter;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private final HttpServer httpServer;
    private final InMemoryTaskManager inMemoryTaskManager;
    private final HistoryManager historyManage;

    public static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .create();

    public HttpTaskServer(InMemoryTaskManager inMemoryTaskManager, HistoryManager historyManager) {
        this.inMemoryTaskManager = inMemoryTaskManager;
        this.historyManage = historyManager;

        try {
            httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        httpServer.createContext("/task", new TaskHttpHandler(inMemoryTaskManager, historyManager));
        httpServer.createContext("/subtask", new SubtaskHttpHandler(inMemoryTaskManager, historyManager));
        httpServer.createContext("/epic", new EpicHttpHandler(inMemoryTaskManager, historyManager));
        httpServer.createContext("/history", new HistoryHttpHandler(inMemoryTaskManager, historyManager));
        httpServer.createContext("/prioritized", new PrioritizedHttpHandler(inMemoryTaskManager, historyManager));
    }

    public void start() {
        httpServer.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Сервер запущен на порту 8080");

    }

    public void stop() {
        httpServer.stop(0);
    }

    public static Gson getGson() {
        return gson;
    }

    public HistoryManager getHistoryManage() {
        return historyManage;
    }

    public InMemoryTaskManager getInMemoryTaskManager() {
        return inMemoryTaskManager;
    }
}


