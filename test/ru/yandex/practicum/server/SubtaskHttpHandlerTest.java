package ru.yandex.practicum.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.HistoryManager;
import ru.yandex.practicum.manager.impl.InMemoryHistoryManager;
import ru.yandex.practicum.manager.impl.InMemoryTaskManager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskHttpHandlerTest {
    InMemoryTaskManager taskManager;
    HistoryManager historyManager;
    HttpTaskServer taskServer;
    Gson gson = HttpTaskServer.getGson();

    private HttpResponse<String> sendPost(String path, String json) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/" + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDelete(String path) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/" + path))
                .DELETE()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
        taskServer = new HttpTaskServer(taskManager, historyManager);
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @DisplayName("Добавление Subtask через сервер появляется в коллекции")
    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        //Given
        HttpResponse<String> response;
        Epic epic = new Epic("Test epic", "Testing epic",
                TaskStatus.NEW);
        String epicJson = gson.toJson(epic);
        sendPost("epic", epicJson);

        //When
        List<Epic> epicsFromManager = taskManager.getAllEpic();
        Subtask subtask = new Subtask("Test subtask", "Testing subtask", TaskStatus.IN_PROGRESS,
                epicsFromManager.getFirst().getTaskId(), LocalDateTime.now(), Duration.ofHours(1));
        epicJson = gson.toJson(subtask);
        response = sendPost("subtask", epicJson);

        //Then
        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getAllSubtask();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test subtask", subtasksFromManager.getFirst().getTaskName(), "Некорректное имя задачи");
    }

    @DisplayName("Удаление в Epic Subtask через сервер меняет его время и статус")
    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        //Given
        HttpResponse<String> response;
        Epic epic = new Epic("Test epic", "Testing epic",
                TaskStatus.NEW);
        String epicJson = gson.toJson(epic);
        sendPost("epic", epicJson);

        List<Epic> epicsFromManager = taskManager.getAllEpic();
        Subtask subtask = new Subtask("Test subtask", "Testing subtask", TaskStatus.IN_PROGRESS,
                epicsFromManager.getFirst().getTaskId(), LocalDateTime.now(), Duration.ofHours(1));
        epicJson = gson.toJson(subtask);
        sendPost("subtask", epicJson);

        //When
        int subtaskId = taskManager.getAllSubtask()
                .getFirst()
                .getTaskId();

        response = sendDelete("subtask/" + subtaskId);

        //Then
        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getAllSubtask();

        assertTrue(subtasksFromManager.isEmpty(), "Subtask не удалился из менеджера");
        assertTrue(epic.getSubtaskForEpic().isEmpty(), "Subtask не удалился из Epic");
    }

    @DisplayName("Обновление Subtask через сервер обновляется в коллекции")
    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        //Given
        HttpResponse<String> response;
        Epic epic = new Epic("Test epic", "Testing epic",
                TaskStatus.NEW);
        String json = gson.toJson(epic);
        sendPost("epic", json);

        List<Epic> epicsFromManager = taskManager.getAllEpic();
        Subtask subtask = new Subtask("Test subtask", "Testing subtask", TaskStatus.IN_PROGRESS,
                epicsFromManager.getFirst().getTaskId(), LocalDateTime.now(), Duration.ofHours(1));
        json = gson.toJson(subtask);
        sendPost("subtask", json);

        //When
        Subtask subtaskUpdate = taskManager.getAllSubtask()
                .getFirst();
        subtaskUpdate.setStatus(TaskStatus.IN_PROGRESS);
        subtaskUpdate.setDuration(Duration.ofHours(2));
        json = gson.toJson(subtaskUpdate);

        response = sendPost("subtask", json);

        //Then
        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getAllSubtask();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(subtaskUpdate.getDuration(), subtasksFromManager.getFirst().getDuration(),
                "Продолжительность не обновилась");
        assertEquals(subtaskUpdate.getStatus(), subtasksFromManager.getFirst().getStatus(),
                "Статус не обновился");
    }
}

