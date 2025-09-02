package ru.yandex.practicum.manager;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.exceptions.TimeConflictException;
import ru.yandex.practicum.manager.impl.FileBackedTaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private Path testFile;

    @BeforeEach
    void setUp() throws IOException {
        testFile = Files.createTempFile("TEST", ".csv");
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(testFile);
    }

    @DisplayName("Менеджер записывает данные в файл и восстанавливает их при перезапуске программы")
    @Test
    void test_Manager_writes_data_and_restores_when_restarted_Test() {
        //Given
        FileBackedTaskManager taskManager = new FileBackedTaskManager(testFile);

        Task task = taskManager.createTask(new Task("Задача 1",  "Информация 1", TaskStatus.NEW));
        int taskID = task.getTaskId();
        Epic epic = taskManager.createEpic(new Epic("Epic 1", "Информация 1", TaskStatus.NEW));
        int epicID = epic.getTaskId();
        Subtask subtask = taskManager.createSubtask(new Subtask("Подзадача 1", "Информация 1",
                TaskStatus.NEW, epicID));
        int subtaskID = subtask.getTaskId();

        //When
        FileBackedTaskManager loaderManager = taskManager.loadFromFile(testFile);

        Optional<Task> loaderTask = loaderManager.getTaskById(taskID);
        Optional<Epic> loaderEpic = loaderManager.getEpicById(epicID);
        Optional<Subtask> loaderSubtask = loaderManager.getSubtaskById(subtaskID);
        Map<Integer, Subtask> subtaskMap = loaderEpic
                .map(Epic::getSubtaskForEpic)
                .orElse(new HashMap<>());

        Subtask subtaskEpic = subtaskMap.get(subtaskID);
        Task loaderTask2 = loaderManager.createTask(new Task("Задача 2", "Информация 2",
                TaskStatus.NEW));
        int loaderTask2ID = loaderTask2.getTaskId();


        //Then
        assertEquals(2, loaderManager.getAllTask().size(), "Задачи не восстановились");
        assertEquals(taskID, loaderManager.getTaskById(taskID)
                        .orElseThrow(() -> new IllegalArgumentException("Epic c ID: " + taskID + " не найден"))
                        .getTaskId()
                , "ID задачи не совпадают");

        assertEquals(1, loaderManager.getAllEpic().size(), "Epic не восстановились");
        assertEquals(epic.getTaskName(), loaderEpic
                        .orElseThrow(() -> new IllegalArgumentException("Epic c ID: " + epicID + " не найден"))
                        .getTaskName(),
                "Имя Epic восстановилось не верно");

        assertEquals(1, loaderManager.getAllSubtask().size(), "Подзадачи не восстановились");
        assertEquals(subtask.getType(), loaderSubtask
                .orElseThrow(() -> new IllegalArgumentException("Subtask c ID: " + subtaskID + " не найден"))
                .getType(), "Тип подзадачи восстановился не верно");

        assertEquals(task, loaderTask
                        .orElseThrow(() -> new IllegalArgumentException("Task c ID: " + taskID + " не найден"))
                , "Задачи не равны");

        assertEquals(subtask, subtaskEpic, "Связи Epic и Подзадач не восстановлены");
        assertTrue(loaderTask2ID > subtaskID, "Счетчик ID восстановился не корректно");
    }

    @DisplayName("Менеджер после загрузки верно восстанавливает статус EPIC")
    @Test
    void test_Manager_correctly_restores_epic_status_after_loading() {
        //Given
        FileBackedTaskManager taskManager = new FileBackedTaskManager(testFile);

        Epic epic = taskManager.createEpic(new Epic("Epic 1", "Информация 1", TaskStatus.NEW));
        int epicID = epic.getTaskId();
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Подзадача 1", "Информация 1",
                TaskStatus.NEW, epicID));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Подзадача 2", "Информация 2",
                TaskStatus.NEW, epicID));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Подзадача 3", "Информация 3",
                TaskStatus.NEW, epicID));

        Epic epic2 = taskManager.createEpic(new Epic("Epic 1", "Информация 1", TaskStatus.NEW));
        int epicID2 = epic2.getTaskId();
        Subtask subtask4 = taskManager.createSubtask(new Subtask("Подзадача 4", "Информация 4",
                TaskStatus.NEW, epicID2));
        Subtask subtask5 = taskManager.createSubtask(new Subtask("Подзадача 5", "Информация 5",
                TaskStatus.NEW, epicID2));
        Subtask subtask6 = taskManager.createSubtask(new Subtask("Подзадача 6", "Информация 6",
                TaskStatus.NEW, epicID2));

        Epic epic3 = taskManager.createEpic(new Epic("Epic 7", "Информация 7", TaskStatus.NEW));
        int epicID3 = epic3.getTaskId();

        //When
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);
        subtask3.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask3);
        subtask4.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask4);
        subtask5.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask5);
        subtask6.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask6);

        FileBackedTaskManager loaderManager = taskManager.loadFromFile(testFile);
        Epic loaderEpic = loaderManager.getEpicById(epicID)
                .orElseThrow(() -> new IllegalArgumentException("Epic c ID: " + epicID + " не найден"));
        Epic loaderEpic2 = loaderManager.getEpicById(epicID2)
                .orElseThrow(() -> new IllegalArgumentException("Epic c ID: " + epicID2 + " не найден"));
        Epic loaderEpic3 = loaderManager.getEpicById(epicID3)
                .orElseThrow(() -> new IllegalArgumentException("Epic c ID: " + epicID3 + " не найден"));


        //Then
        assertEquals(TaskStatus.IN_PROGRESS, loaderEpic.getStatus(), "Статус восстановился не верно");
        assertEquals(TaskStatus.DONE, loaderEpic2.getStatus(), "Статус восстановился не верно");
        assertEquals(TaskStatus.NEW, loaderEpic3.getStatus(), "Статус восстановился не верно");

    }

    @DisplayName("Менеджер после загрузки восстанавливает сортировку и TimeControl")
    @Test
    void shouldRestoreSortedListAndTimeControl_whenManagerLoaded() {
        //Given
        FileBackedTaskManager taskManager = new FileBackedTaskManager(testFile);

        Task task = taskManager.createTask(new Task("Задача 1", "Информация 1", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 0, 0), Duration.ofHours(1)));
        Epic epic = taskManager.createEpic(new Epic("Epic 1", "Информация 1", TaskStatus.NEW));
        Subtask subtask = taskManager.createSubtask(new Subtask("Подзадача 1", "Информация 1", TaskStatus.NEW,
                epic.getTaskId(),
                LocalDateTime.of(2025, 1, 2, 0, 0), Duration.ofHours(1)));


        //When
        FileBackedTaskManager loaderManager = taskManager.loadFromFile(testFile);

        //Then
        assertTrue(loaderManager.getPrioritizedTasks().contains(task), "Задача не восстановилась в сортировке");
        assertTrue(loaderManager.getPrioritizedTasks().contains(epic), "Задача не восстановилась в сортировке");
        assertTrue(loaderManager.getPrioritizedTasks().contains(subtask),
                "Задача не восстановилась в сортировке");

        assertEquals(loaderManager.getPrioritizedTasks().getFirst(), task, "Сортировка сбилась");
        assertEquals(loaderManager.getPrioritizedTasks().getLast(), subtask, "Сортировка сбилась");

        task.setStartTime(LocalDateTime.of(2025, 1, 2, 0, 0));
        task.setDuration(Duration.ofHours(1));

        Assertions.assertThrows(TimeConflictException.class, () -> loaderManager.updateTask(task),
                "TimeControl не восстановился");

        task.setStartTime(LocalDateTime.of(2025, 1, 3, 0, 0));
        task.setDuration(Duration.ofHours(1));

        Assertions.assertDoesNotThrow(() -> loaderManager.updateTask(task),
                "TimeControl не восстановился");

    }

}