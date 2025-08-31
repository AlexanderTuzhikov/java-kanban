package ru.yandex.practicum.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.HistoryManager;
import ru.yandex.practicum.manager.impl.InMemoryHistoryManager;
import ru.yandex.practicum.manager.impl.InMemoryTaskManager;

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

class TaskHttpHandlerTest {
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

    @DisplayName("Добавление Task через сервер появляется в коллекции")
    @Test
    public void testAddTask() throws IOException, InterruptedException {
        //Given
        Task task = new Task("Test task", "Testing task",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        //When
        HttpResponse<String> response = sendPost("task", taskJson);

        //Then
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTask();
        System.out.println(tasksFromManager);
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test task", tasksFromManager.getFirst().getTaskName(), "Некорректное имя задачи");
    }

    @DisplayName("Обновленный Task через сервер появляется в коллекции")
    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        //Given
        HttpResponse<String> response;
        Task task = new Task("Test task", "Testing task",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);
        sendPost("task", taskJson);

        //When
        Task updateTask = taskManager.getAllTask().getFirst();
        updateTask.setDuration(Duration.ofHours(1));

        taskJson = gson.toJson(updateTask);

        response = sendPost("task", taskJson);

        //Then
        assertEquals(200, response.statusCode());

        assertNotNull(taskManager.getAllTask(), "Задачи не возвращаются");
        assertEquals(1, taskManager.getAllTask().size(), "Некорректное количество задач");
        assertEquals(taskManager.getAllTask().getFirst().getDuration(), Duration.ofHours(1), "Некорректное время задачи");
    }

    @DisplayName("Удаление Task через сервер удаляет из коллекции")
    @Test
    public void deleteTask() throws IOException, InterruptedException {
        //Given
        Task task = new Task("Test task", "Testing task",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);
        HttpResponse<String> response;
        sendPost("task", taskJson);

        //When
        Task deleteTask = taskManager.getAllTask().getFirst();
        int taskId = deleteTask.getTaskId();

        response = sendDelete("task/" + taskId);

        //Then
        assertEquals(200, response.statusCode());

        assertTrue(taskManager.getAllTask().isEmpty(), "Задачи не удаляются из менеджера");
        assertTrue(taskManager.getPrioritizedTasks().isEmpty(), "Задачи не удаляются из Prioritized");
    }
}