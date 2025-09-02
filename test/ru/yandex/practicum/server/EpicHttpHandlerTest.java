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

class EpicHttpHandlerTest {
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
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        taskServer = new HttpTaskServer(taskManager, historyManager);
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @DisplayName("Добавление Epic через сервер появляется в коллекции")
    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        //Given
        HttpResponse<String> response;
        Epic epic = new Epic("Test epic", "Testing epic",
                TaskStatus.NEW);
        String epicJson = gson.toJson(epic);

        //When
        response = sendPost("epic", epicJson);

        //Then
        assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getAllEpic();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test epic", tasksFromManager.getFirst().getTaskName(), "Некорректное имя задачи");
    }

    @DisplayName("Добавление в Epic Subtask через сервер меняет его время и статус")
    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        //Given
        HttpResponse<String> response;
        Epic epic = new Epic("Test epic", "Testing epic",
                TaskStatus.NEW);
        String epicJson = gson.toJson(epic);
        sendPost("epic", epicJson);

        //When
        List<Epic> tasksFromManager = taskManager.getAllEpic();
        Subtask subtask = new Subtask("Test subtask", "Testing subtask", TaskStatus.IN_PROGRESS,
                tasksFromManager.getFirst().getTaskId(), LocalDateTime.now(), Duration.ofHours(1));
        epicJson = gson.toJson(subtask);
        response = sendPost("subtask", epicJson);

        //Then
        assertEquals(200, response.statusCode());

        tasksFromManager = taskManager.getAllEpic();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(subtask.getStatus(), tasksFromManager.getFirst().getStatus(),
                "Статус не совпадает");
        assertEquals(subtask.getDuration(), tasksFromManager.getFirst().getDuration(),
                "Продолжительность не совпадает");
        assertEquals(subtask.getStartTime(), tasksFromManager.getFirst().getStartTime(),
                "Время начала не совпадает");
        assertEquals(subtask.getEndTime(), tasksFromManager.getFirst().getEndTime(),
                "Время окончания не совпадает");
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

        List<Epic> tasksFromManager = taskManager.getAllEpic();
        Subtask subtask = new Subtask("Test subtask", "Testing subtask", TaskStatus.IN_PROGRESS,
                tasksFromManager.getFirst().getTaskId(), LocalDateTime.now(), Duration.ofHours(1));
        epicJson = gson.toJson(subtask);
        sendPost("subtask", epicJson);

        //When

        int subtaskId = taskManager.getAllSubtask()
                .getFirst()
                .getTaskId();
        response = sendDelete("subtask/" + subtaskId);

        //Then
        assertEquals(200, response.statusCode());

        tasksFromManager = taskManager.getAllEpic();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(TaskStatus.NEW, tasksFromManager.getFirst().getStatus(),
                "Статус не совпадает");
        assertNull(tasksFromManager.getFirst().getDuration(), "Продолжительность не сбросилась");
        assertNull(tasksFromManager.getFirst().getStartTime(), "Время начала не сбросилось");
        assertNull(tasksFromManager.getFirst().getEndTime(), "Время окончания не сбросилось");
    }

    @DisplayName("Удаление Epic через сервер удаляет его из коллекций и его Subtask")
    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        //Given
        HttpResponse<String> response;
        Epic epic = new Epic("Test epic", "Testing epic",
                TaskStatus.NEW);
        String epicJson = gson.toJson(epic);
        sendPost("epic", epicJson);

        List<Epic> tasksFromManager = taskManager.getAllEpic();
        Subtask subtask = new Subtask("Test subtask", "Testing subtask", TaskStatus.IN_PROGRESS,
                tasksFromManager.getFirst().getTaskId(), LocalDateTime.now(), Duration.ofHours(1));
        epicJson = gson.toJson(subtask);
        sendPost("subtask", epicJson);

        //When
        int epicId = taskManager.getAllEpic()
                .getFirst()
                .getTaskId();

        response = sendDelete("epic/" + epicId);

        //Then
        assertEquals(200, response.statusCode());

        assertTrue(taskManager.getAllEpic().isEmpty(), "Epic е удалился из менеджера");
        assertTrue(taskManager.getAllSubtask().isEmpty(), "Subtask не удалился из менеджера");
        assertTrue(taskManager.getPrioritizedTasks().isEmpty(), "Epic и Subtask не удалились из Prioritized");
    }
}