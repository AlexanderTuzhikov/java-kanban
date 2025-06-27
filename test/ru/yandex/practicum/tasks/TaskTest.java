package ru.yandex.practicum.tasks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.util.Stubs;

import static org.testng.Assert.*;

class TaskTest {

    Task testTask = new Task(1, "testName","testInfo");

    @DisplayName("Поля задач проинициализированы верно при создании")
    @Test
    void test_Task_Fields_Are_Initialized_Correctly_Test() {
        //Given
        int TEST_ID = 1;
        String TEST_NAME = "testName";
        String TEST_INFO = "testInfo";

        //When
        int TASK_ID = testTask.getTaskId();
        String TASK_NAME = testTask.getTaskName();
        String TASK_INFO = testTask.getTaskInfo();

        //Then
        assertEquals(TEST_ID, TASK_ID,
                "ID задачи не верен");
        assertEquals(TEST_NAME, TASK_NAME,
                "Имя задачи не верно");
        assertEquals(TEST_INFO, TASK_INFO,
                "Информация о задаче не верна");
    }

    @DisplayName("Установка статуса задачи работает корректно")
    @Test
    void test_Set_Status_Correctly_Test() {
        //Given
        TaskStatus TASK_STATUS_TEST = TaskStatus.IN_PROGRESS;

        //When
        Stubs.testTask2.setStatus(TaskStatus.IN_PROGRESS);

        //Then
        assertEquals(TASK_STATUS_TEST, Stubs.testTask2.getStatus(),
                "Установка статуса работает не корректно");
    }

    @DisplayName("Установка имени и информации задач работает корректно")
    @Test
    void test_Set_Name_And_Set_Info_Correctly_Test() {
        //Given
        String TEST_NAME = "New Test Name";
        String TEST_INFO = "New Test Info";

        //When
        Stubs.testTask3.setTaskName("New Test Name");
        Stubs.testTask3.setTaskInfo("New Test Info");

        //Then
        assertEquals(TEST_NAME, Stubs.testTask3.getTaskName(),
                 "Установка нового имени работает не корректно");
        assertEquals(TEST_INFO, Stubs.testTask3.getTaskInfo(),
                "Установка новой информации работает не корректно");
    }

    @DisplayName("Одинаковые задачи равны по полям")
    @Test
    void test_Identical_Tasks_By_Fields_Are_Compared_Correctly_Test() {
        //Given
        String TEST_NAME = Stubs.testTask1.getTaskName();
        String TEST_INFO = Stubs.testTask1.getTaskInfo();
        TaskStatus TEST_STATUS = Stubs.testTask1.getStatus();

        //When
        Stubs.testTask2.setTaskInfo(TEST_INFO);
        Stubs.testTask2.setTaskName(TEST_NAME);
        Stubs.testTask2.setStatus(TEST_STATUS);

        //Then
        assertEquals(Stubs.testTask2, Stubs.testTask1,
                "Задачи не равны");
    }
}