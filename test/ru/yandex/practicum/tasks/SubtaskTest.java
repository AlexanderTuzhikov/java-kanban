package ru.yandex.practicum.tasks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.util.Managers;
import ru.yandex.practicum.util.Stubs;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @DisplayName("Поля подзадач проинициализированы верно при создании")
    @Test
    void test_Subtask_Fields_Are_Initialized_Correctly_Test() {
        //Given
        int TEST_ID = 24;
        String TEST_NAME = "testName1";
        String TEST_INFO = "TestInfo1";

        //
        TaskManager taskManager = Managers.getDefault();
        Task testSubtask1 = taskManager.createTask(Stubs.testSubtask1);
        int SUBTASK_ID = testSubtask1.getTaskId();
        String SUBTASK_NAME = testSubtask1.getTaskName();
        String SUBTASK_INFO = testSubtask1.getTaskInfo();
        //Then
        assertEquals(TEST_ID, SUBTASK_ID,
                "ID задачи не верен");
        assertEquals(TEST_NAME, SUBTASK_NAME,
                "Имя задачи не верно");
        assertEquals(TEST_INFO, SUBTASK_INFO,
                "Информация о задаче не верна");
    }

    @DisplayName("Установка статуса подзадачи работает корректно")
    @Test
    void test_Set_Status_Correctly_Test() {
        //Given
        TaskStatus TEST_STATUS = TaskStatus.IN_PROGRESS;

        //When
        Stubs.testSubtask2.setStatus(TaskStatus.IN_PROGRESS);

        //Then
        assertEquals(TEST_STATUS, Stubs.testSubtask2.getStatus(),
                "Установка статуса работает не корректно");
    }

    @DisplayName("Установка имени и информации подзадач работает корректно")
    @Test
    void test_Set_Name_And_Set_Info_Correctly_Test() {
        //Given
        String TEST_NAME = "New Test Name";
        String TEST_INFO = "New Test Info";

        //When
        Stubs.testSubtask3.setTaskName("New Test Name");
        Stubs.testSubtask3.setTaskInfo("New Test Info");
        String TASK_NAME = Stubs.testSubtask3.getTaskName();
        String TASK_INFO = Stubs.testSubtask3.getTaskInfo();

        //Then
        assertEquals(TEST_NAME, TASK_NAME, "Установка нового имени работает не корректно");
        assertEquals(TEST_INFO, TASK_INFO, "Установка новой информации работает не корректно");
    }

    @DisplayName("Одинаковые подзадачи равны по полям")
    @Test
    void test_Identical_Subtasks_By_Fields_Are_Compared_Correctly_Test() {
        //Given
        String TEST_INFO = Stubs.testSubtask2.getTaskInfo();
        String TEST_NAME = Stubs.testSubtask2.getTaskName();
        TaskStatus TEST_STATUS = Stubs.testSubtask2.getStatus();

        //When
        Stubs.testSubtask4.setTaskInfo(TEST_INFO);
        Stubs.testSubtask4.setTaskName(TEST_NAME);
        Stubs.testSubtask4.setStatus(TEST_STATUS);

        //Then
        assertEquals(Stubs.testSubtask2, Stubs.testSubtask4,
                "Задачи не равны");
    }

    @DisplayName("Метод вызова ID Epic подзадачи работает корректно")
    @Test
    void test_Get_Epic_Id_Correctly_Test() {
        //Given
        int TEST_EPIC_ID = 3;

        //When
        int EPIC_ID = Stubs.testSubtask3.getEpicId();

        //Then
        assertEquals(TEST_EPIC_ID, EPIC_ID, "Метод вызова EpicID работает не корректно ");
    }
}
