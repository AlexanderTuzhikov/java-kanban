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


class PrioritizedHttpHandlerTest {
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

    @DisplayName("Добавление Task через сервер появляется в Prioritized")
    @Test
    public void get_Prioritized_Task() throws IOException, InterruptedException {
        //Given
        Task task = new Task("Test task", "Testing task",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);


        //When
        HttpResponse<String> response = sendPost("task", taskJson);

        //Then
        assertEquals(200, response.statusCode());
        assertEquals(1, taskManager.getPrioritizedTasks().size(),"Задачи добавляются в Prioritized");
    }

    @DisplayName("Удаление Task через сервер удаляется в Prioritized")
    @Test
    public void get_Prioritized_After_Remove_Task() throws IOException, InterruptedException {
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
        response = sendDelete("task/" + taskId);

        //Then
        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getPrioritizedTasks().isEmpty(),"Task не удаляются в Prioritized");
    }

    @DisplayName("Добавление Subtask через сервер появляется в Prioritized")
    @Test
    public void get_Prioritized_Subtask() throws IOException, InterruptedException {
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
        assertEquals(2, taskManager.getPrioritizedTasks().size(),"Subtask не добавляются в Prioritized");
    }

    @DisplayName("Удаление Subtask через сервер удаляется в Prioritized")
    @Test
    public void get_Prioritized_After_Remove_Subtask() throws IOException, InterruptedException {
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
        assertTrue(taskManager.getPrioritizedTasks().isEmpty(),"Subtask не удаляются в Prioritized");
    }

}