package ru.yandex.practicum;

import ru.yandex.practicum.manager.impl.InMemoryHistoryManager;
import ru.yandex.practicum.manager.impl.InMemoryTaskManager;

import ru.yandex.practicum.server.HttpTaskServer;
import ru.yandex.practicum.util.Managers;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        InMemoryTaskManager taskManager = Managers.getDefault();

        InMemoryHistoryManager historyManager = Managers.getDefaultHistory();

        try {
            HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager, historyManager);
            httpTaskServer.start();
            System.out.println("Нажмите Enter для остановки...");

            System.in.read();

            httpTaskServer.stop();
            System.out.println("HTTP-сервер остановлен");
        } catch (IOException exception) {
            exception.getStackTrace();
        }
    }
}