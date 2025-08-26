package ru.yandex.practicum.manager;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.exceptions.TimeConflictException;
import ru.yandex.practicum.tasks.*;
import ru.yandex.practicum.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {
    private final TaskManager taskManager = Managers.getDefault();

    private static final Duration TEST_DURATION = Duration.ofHours(1);
    private static final LocalDateTime TEST_DATE_TIME = LocalDateTime.of(2025, 1, 1, 0, 0);
    private static final Duration TEST_CORRECT_DATE_TIME = Duration.ofDays(1);
    private static final Duration TEST_CORRECT_DURATION = Duration.ofHours(1);


    @BeforeEach
    void setUp() {
        taskManager.removeAllTask();
        taskManager.removeAllSubtask();
        taskManager.removeAllEpic();
    }

    @DisplayName("Через утилитарный класс Managers должны корректно создаваться задачи разных типов")
    @Test
    void create_Task_Subtask_Epic() {
        //Given
        Task testTask1;
        Epic testEpic1;
        Subtask testSubtask1;

        //When
        testTask1 = taskManager.createTask(new Task("Test name", "Test info", TaskStatus.NEW));
        testEpic1 = taskManager.createEpic(new Epic("Test name", "Test info", TaskStatus.NEW));
        testSubtask1 = taskManager.createSubtask(new Subtask("Test name", "Test info", TaskStatus.NEW,
                testEpic1.getTaskId()));


        //Then
        assertNotNull(testTask1, "Задача создана некорректно");
        assertNotNull(testSubtask1, "Подзадача создана некорректно");
        assertNotNull(testEpic1, "Epic создан некорректно");
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                        taskManager.createSubtask(new Subtask("Test name", "Test info", TaskStatus.NEW, 10)),
                "Не должна быть создана");
    }

    @DisplayName("Время устанавливается и рассчитывается корректно для разных типов задач")
    @Test
    void givenDifferentTaskTypes_whenTimeIsSet_thenCalculatedCorrectly() {
        //Given
        Task testTask1;
        Epic testEpic1;
        Subtask testSubtask1;
        Subtask testSubtask2;

        //When
        testTask1 = taskManager.createTask(new Task("Test name", "Test info", TaskStatus.NEW,
                TEST_DATE_TIME, TEST_DURATION));
        testEpic1 = taskManager.createEpic(new Epic("Test name", "Test info", TaskStatus.NEW));
        testSubtask1 = taskManager.createSubtask(new Subtask("Test name", "Test info", TaskStatus.NEW,
                testEpic1.getTaskId(), TEST_DATE_TIME.plus(TEST_CORRECT_DATE_TIME), TEST_DURATION));
        testSubtask2 = taskManager.createSubtask(new Subtask("Test name", "Test info", TaskStatus.NEW,
                testEpic1.getTaskId(), TEST_DATE_TIME.plus(TEST_CORRECT_DATE_TIME.plus(TEST_DURATION)), TEST_DURATION.plus(TEST_CORRECT_DURATION)
        ));

        //Then
        assertEquals(TEST_DATE_TIME, testTask1.getStartTime(),
                "Время старта установилось не корректно");
        assertEquals(TEST_DURATION, testTask1.getDuration(),
                "Время продолжительности задачи установилось не корректно");
        assertEquals(TEST_DATE_TIME.plus(TEST_DURATION),
                testTask1.getEndTime(), "Время финиша установилось не корректно");
        assertEquals(testSubtask1.getStartTime(), testEpic1.getStartTime(),
                "Время старта Epic рассчиталось не корректно");
        assertEquals(testSubtask1.getDuration().plus(testSubtask2.getDuration()), testEpic1.getDuration(),
                "Время продолжительности Epic рассчиталось не корректно");
        assertEquals(testSubtask2.getEndTime(), testEpic1.getEndTime(),
                "Время финала Epic рассчиталось не корректно");

    }

    @DisplayName("При удалении подзадачи время Epic пересчитывается верно")
    @Test
    void givenEpicWithSubtasks_whenSubtaskDeleted_thenTimeRecalculatedCorrectly() {
        //Given
        Epic testEpic1;
        Subtask testSubtask1;
        Subtask testSubtask2;

        //When
        testEpic1 = taskManager.createEpic(new Epic("Test name", "Test info", TaskStatus.NEW));
        testSubtask1 = taskManager.createSubtask(new Subtask("Test name", "Test info", TaskStatus.NEW,
                testEpic1.getTaskId(), TEST_DATE_TIME.plus(TEST_CORRECT_DATE_TIME), TEST_DURATION));
        testSubtask2 = taskManager.createSubtask(new Subtask("Test name", "Test info", TaskStatus.NEW,
                testEpic1.getTaskId(), TEST_DATE_TIME.plus(TEST_CORRECT_DATE_TIME.plus(TEST_DURATION)), TEST_DURATION.plus(TEST_CORRECT_DURATION)
        ));
        taskManager.removeSubtaskById(testSubtask1.getTaskId());


        //Then
        assertEquals(testSubtask2.getStartTime(), testEpic1.getStartTime(),
                "Время старта Epic рассчиталось не корректно");
        assertEquals(testSubtask2.getDuration(), testEpic1.getDuration(),
                "Время продолжительности Epic рассчиталось не корректно");
        assertEquals(testSubtask2.getEndTime(), testEpic1.getEndTime(),
                "Время финала Epic рассчиталось не корректно");
    }

    @DisplayName("Конфликт времени работает корректно")
    @Test
    void givenOverlappingTasks_whenTimeSet_thenConflictDetected() {
        //Given
        Task testTask1;

        //When
        testTask1 = taskManager.createTask(new Task("Test name", "Test info", TaskStatus.NEW,
                TEST_DATE_TIME, TEST_DURATION));

        //Then
        Assertions.assertThrows(TimeConflictException.class,
                () -> taskManager.createTask(new Task("Test name", "Test info", TaskStatus.NEW,
                        TEST_DATE_TIME, TEST_DURATION)),
                "Должен быть конфликт");
    }

    @DisplayName("Слоты контроля освобождаются высвобождаются при удалении задачи")
    @Test
    void givenTaskWithTimeSlot_whenTaskDeleted_thenSlotIsFreed() {
        //Given
        Task testTask1;

        //When
        testTask1 = taskManager.createTask(new Task("Test name", "Test info", TaskStatus.NEW,
                TEST_DATE_TIME, TEST_DURATION));
        taskManager.removeTaskById(testTask1.getTaskId());

        //Then
        Assertions.assertDoesNotThrow(
                () -> taskManager.createTask(new Task("Test name", "Test info", TaskStatus.NEW,
                        TEST_DATE_TIME, TEST_DURATION)),
                "Конфликта быть не должно  быть конфликт");
    }

    @DisplayName("Сортировка задач по старту работает верно")
    @Test
    void shouldReturnTasksSortedByStartTime() {
        //Given
        Task testTask1;
        Task testTask2;
        Task testTask3;

        //When
        testTask1 = taskManager.createTask(new Task ("Test name", "Test info", TaskStatus.NEW,
                TEST_DATE_TIME, TEST_DURATION));
        testTask2 = taskManager.createTask(new Task ("Test name", "Test info", TaskStatus.NEW,
                TEST_DATE_TIME.plus(TEST_CORRECT_DATE_TIME), TEST_DURATION));
        testTask3 = taskManager.createTask(new Task ("Test name", "Test info", TaskStatus.NEW,
                TEST_DATE_TIME.plusDays(2), TEST_DURATION));
        List<Task> sortedList = taskManager.getPrioritizedTasks();

        //Then
        assertEquals(testTask1, sortedList.getFirst(), "Сортировка не корректна");
        assertEquals(testTask3, sortedList.getLast(), "Сортировка не корректна");
    }

    @DisplayName("При удалении задачи они удаляются из сортировки")
    @Test
    void givenTasksInSortedList_whenDeleted_thenTheyAreRemoved() {
        //Given
        Task testTask1;
        Task testTask2;
        Task testTask3;

        //When
        testTask1 = taskManager.createTask(new Task ("Test name", "Test info", TaskStatus.NEW,
                TEST_DATE_TIME, TEST_DURATION));
        testTask2 = taskManager.createTask(new Task ("Test name", "Test info", TaskStatus.NEW,
                TEST_DATE_TIME.plus(TEST_CORRECT_DATE_TIME), TEST_DURATION));
        testTask3 = taskManager.createTask(new Task ("Test name", "Test info", TaskStatus.NEW,
                TEST_DATE_TIME.plusDays(2), TEST_DURATION));

        taskManager.removeTaskById(testTask1.getTaskId());

        //Then
        assertFalse(taskManager.getPrioritizedTasks().contains(testTask1), "Задача удалена из списка");
        assertEquals(testTask2, taskManager.getPrioritizedTasks().getFirst(), "Сортировка обновилась");
        assertEquals(testTask3, taskManager.getPrioritizedTasks().getLast(), "Сортировка не корректна");
    }

    @DisplayName("При удалении задачи они удаляются из сортировки")
    @Test
    void givenEpicWithSubtasks_whenEpicDeleted_thenSubtasksRemovedFromSortedListAndTimeControl() {
        Epic testEpic1;
        Subtask testSubtask2;
        Subtask testSubtask3;
        Task testTask1;

        //When
        testEpic1 = taskManager.createEpic(new Epic("Test name", "Test info", TaskStatus.NEW));
        testSubtask2 = taskManager.createSubtask(new Subtask("Test name", "Test info", TaskStatus.NEW,
                testEpic1.getTaskId(), TEST_DATE_TIME.plus(TEST_CORRECT_DATE_TIME), TEST_DURATION));
        testSubtask3 = taskManager.createSubtask(new Subtask("Test name", "Test info", TaskStatus.NEW,
                testEpic1.getTaskId(), TEST_DATE_TIME.plusDays(2), TEST_DURATION));
        testTask1 = taskManager.createTask(new Task ("Test name","Test info", TaskStatus.NEW));
        taskManager.removeEpicById(testEpic1.getTaskId());


        assertFalse(taskManager.getPrioritizedTasks().contains(testEpic1),
                "Epic не удален из сортировки");
        assertFalse(taskManager.getPrioritizedTasks().contains(testSubtask2),
                "Subtask не удален из сортировки");
        assertFalse(taskManager.getPrioritizedTasks().contains(testSubtask3),
                "Subtask не удален из сортировки");

        testTask1.setDuration(TEST_DURATION);
        testTask1.setStartTime(TEST_DATE_TIME);

        Assertions.assertDoesNotThrow(() -> taskManager.updateTask(testTask1),
                "Слот Epic не освободился в контроле");

        testTask1.setDuration(TEST_DURATION);
        testTask1.setStartTime(TEST_DATE_TIME.plus(TEST_CORRECT_DATE_TIME));

        Assertions.assertDoesNotThrow(() -> taskManager.updateTask(testTask1),
                "Слот Subtask не освободился в контроле");

        testTask1.setDuration(TEST_DURATION);
        testTask1.setStartTime(TEST_DATE_TIME.plusDays(2));

        Assertions.assertDoesNotThrow(() -> taskManager.updateTask(testTask1),
                "Слот Subtask не освободился в контроле");
    }
}



