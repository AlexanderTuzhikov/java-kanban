package ru.yandex.practicum.manager;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

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

        Task task = taskManager.createNewTask("Задача 1", "Информация 1");
        int taskID = task.getTaskId();
        Epic epic = taskManager.createNewEpic("Epic 1", "Информация 1");
        int epicID = epic.getTaskId();
        Subtask subtask = taskManager.createNewSubtask("Подзадача 1", "Информация 1", epicID);
        int subtaskID = subtask.getTaskId();

        //When
        FileBackedTaskManager loaderManager = FileBackedTaskManager.loadFromFile(testFile);

        Task loaderTask = loaderManager.getTaskById(taskID);
        Epic loaderEpic = loaderManager.getEpicById(epicID);
        Subtask loaderSubtask = loaderManager.getSubtaskById(subtaskID);
        Map<Integer, Subtask> subtaskMap = loaderEpic.getSubtaskForEpic();
        Subtask subtaskEpic = subtaskMap.get(subtaskID);
        Task loaderTask2 = loaderManager.createNewTask("Задача 2", "Информация 2");
        int loaderTask2ID = loaderTask2.getTaskId();


        //Then
        assertEquals(2, loaderManager.getAllTask().size(), "Задачи не восстановились");
        assertEquals(taskID, loaderManager.getTaskById(taskID).getTaskId(), "ID задачи не совпадают");
        assertEquals(1, loaderManager.getAllEpic().size(), "Epic не восстановились");
        assertEquals(epic.getTaskName(), loaderEpic.getTaskName(), "Имя Epic восстановилось не верно");
        assertEquals(1, loaderManager.getAllSubtaskTask().size(), "Подзадачи не восстановились");
        assertEquals(subtask.getType(), loaderSubtask.getType(), "Тип подзадачи восстановился не верно");
        assertEquals(task, loaderTask, "Задачи не равны");
        assertEquals(subtask, subtaskEpic, "Связи Epic и Подзадач не восстановлены");
        assertTrue(loaderTask2ID > subtaskID, "Счетчик ID восстановился не корректно");
    }

    @DisplayName("Менеджер после загрузки верно восстанавливает статус EPIC")
    @Test
    void test_Manager_correctly_restores_epic_status_after_loading() {
        //Given
        FileBackedTaskManager taskManager = new FileBackedTaskManager(testFile);

        Epic epic = taskManager.createNewEpic("Epic 1", "Информация 1");
        int epicID = epic.getTaskId();
        Subtask subtask1 = taskManager.createNewSubtask("Подзадача 1", "Информация 1", epicID);
        Subtask subtask2 = taskManager.createNewSubtask("Подзадача 2", "Информация 2", epicID);
        Subtask subtask3 = taskManager.createNewSubtask("Подзадача 3", "Информация 3", epicID);

        Epic epic2 = taskManager.createNewEpic("Epic 1", "Информация 1");
        int epicID2 = epic2.getTaskId();
        Subtask subtask4 = taskManager.createNewSubtask("Подзадача 4", "Информация 4", epicID2);
        Subtask subtask5 = taskManager.createNewSubtask("Подзадача 5", "Информация 5", epicID2);
        Subtask subtask6 = taskManager.createNewSubtask("Подзадача 6", "Информация 6", epicID2);

        Epic epic3 = taskManager.createNewEpic("Epic 7", "Информация 7");
        int epicID3 = epic3.getTaskId();
        Subtask subtask7 = taskManager.createNewSubtask("Подзадача 7", "Информация 7", epicID3);

        //When
        taskManager.updateSubtask(subtask1, TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask2, TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask3, TaskStatus.DONE);

        taskManager.updateSubtask(subtask4, TaskStatus.DONE);
        taskManager.updateSubtask(subtask5, TaskStatus.DONE);
        taskManager.updateSubtask(subtask6, TaskStatus.DONE);

        FileBackedTaskManager loaderManager = FileBackedTaskManager.loadFromFile(testFile);
        Epic loaderEpic = loaderManager.getEpicById(epicID);
        Epic loaderEpic2 = loaderManager.getEpicById(epicID2);
        Epic loaderEpic3 = loaderManager.getEpicById(epicID3);

        //Then
        assertEquals(TaskStatus.IN_PROGRESS, loaderEpic.getStatus(), "Статус восстановился не верно");
        assertEquals(TaskStatus.DONE, loaderEpic2.getStatus(), "Статус восстановился не верно");
        assertEquals(TaskStatus.NEW, loaderEpic3.getStatus(), "Статус восстановился не верно");

    }
}