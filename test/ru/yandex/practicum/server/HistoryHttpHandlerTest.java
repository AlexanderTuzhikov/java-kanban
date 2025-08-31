package ru.yandex.practicum.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.impl.InMemoryHistoryManager;
import ru.yandex.practicum.manager.impl.InMemoryTaskManager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
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

class HistoryHttpHandlerTest {
    InMemoryTaskManager taskManager;
    InMemoryHistoryManager historyManager;
    HttpTaskServer taskServer;
    Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
        taskServer = new HttpTaskServer(taskManager, historyManager);
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

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

    private HttpResponse<String> sendGet(String path) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/" + path))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @DisplayName("Get Task добавляет его в историю")
    @Test
    public void test_Get_Task() throws IOException, InterruptedException {
        //Given
        HttpResponse<String> response;
        Task task = new Task("Test task", "Testing task",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);
        sendPost("task", taskJson);

        //When
        int taskId = taskManager.getAllTask()
                .getFirst()
                .getTaskId();
        response = sendGet("task/" + taskId);

        //Then
        assertEquals(200, response.statusCode());

        assertEquals(1, historyManager.getHistory().size(), "Task не добавился в History");
        sendDelete("task/" + taskId);
        assertTrue(historyManager.getHistory().isEmpty(), "Task при удалении не пропадает из History");
    }

    @DisplayName("Get Epic добавляет его в историю")
    @Test
    public void test_Get_Epic() throws IOException, InterruptedException {
        //Given
        HttpResponse<String> response;
        Epic epic = new Epic("Test epic", "Testing epic",
                TaskStatus.NEW);
        String epicJson = gson.toJson(epic);
        sendPost("epic", epicJson);

        //When
        int epicId = taskManager.getAllEpic()
                .getFirst()
                .getTaskId();
        response = sendGet("epic/" + epicId);

        //Then
        assertEquals(200, response.statusCode());

        assertEquals(1, historyManager.getHistory().size(), "Epic не добавился в History");
        sendDelete("epic/" + epicId);
        assertTrue(historyManager.getHistory().isEmpty(), "Epic при удалении не пропадает из History");
    }


    @DisplayName("Get Subtask добавляет его в историю")
    @Test
    public void test_Get_Subtask() throws IOException, InterruptedException {
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
        response = sendGet("subtask/" + subtaskId);

        //Then
        assertEquals(200, response.statusCode());
        assertEquals(2, historyManager.getHistory().size(), "Subtask не добавился в History");
        sendDelete("subtask/" + subtaskId);
        System.out.println(historyManager.getHistory());
        assertEquals(1, historyManager.getHistory().size(), "Subtask не удалился в History");
    }
}