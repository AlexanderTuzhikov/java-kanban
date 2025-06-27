package ru.yandex.practicum.tasks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.util.Stubs;

import static org.testng.Assert.*;

class EpicTest {

    @DisplayName("При создании задач конструктор присваивает верные значения полям")
    @Test
    void test_Epic_Fields_Are_Initialized_Correctly_Test() {
        //Given
        int TEST_EPIC_ID = 1;
        String TASK_NAME_TEST = "testName1";
        String TEST_TASK_INFO = "TestInfo1";

        //When
        int EPIC_ID = Stubs.testEpic1.getTaskId();
        String TASK_NAME = Stubs.testEpic1.getTaskName();
        String TASK_INFO = Stubs.testEpic1.getTaskInfo();

        //Then
        assertEquals(EPIC_ID, TEST_EPIC_ID,
                "ID задачи не верен");
        assertEquals(TASK_NAME, TASK_NAME_TEST,
                "Имя задачи не верно");
        assertEquals( TASK_INFO, TEST_TASK_INFO,
                "Информация о задаче не верна");
    }

    @DisplayName("Метод изменения статуса задач работает корректно")
    @Test
    void test_Set_Status_Correctly_Test() {
        //Given
        TaskStatus TEST_TASK_STATUS = TaskStatus.IN_PROGRESS;

        //When
        Stubs.testEpic2.setStatus(TaskStatus.IN_PROGRESS);

        //Then
        assertEquals(Stubs.testEpic2.getStatus(), TEST_TASK_STATUS,
                "Установка статуса работает не корректно");
    }

    @DisplayName("Методы изменения имени и дополнительной информации для задач работают корректно")
    @Test
    void test_Set_Name_And_Set_Info_Correctly_Test() {
        //Given
        String TEST_TASK_NAME = "New Test Name";
        String TEST_TASK_INFO = "New Test Info";

        //When
        Stubs.testEpic3.setTaskName("New Test Name");
        Stubs.testEpic3.setTaskInfo("New Test Info");

        //Then
        assertEquals(Stubs.testEpic3.getTaskName(), TEST_TASK_NAME
                , "Установка нового имени работает не корректно");
        assertEquals(Stubs.testEpic3.getTaskInfo(), TEST_TASK_INFO,
                "Установка новой информации работает не корректно");
    }

    @DisplayName("Задачи сравниваются по полям не по Hash Code")
    @Test
    void test_identical_Epics_By_Fields_Are_Compared_Correctly_Test() {
        //Given
        String TEST_TASK_INFO = Stubs.testEpic1.getTaskInfo();
        String TEST_TASK_NAME = Stubs.testEpic1.getTaskName();
        TaskStatus TEST_TASK_STATUS = Stubs.testEpic1.getStatus();

        //When
        Stubs.testEpic2.setTaskInfo(TEST_TASK_INFO);
        Stubs.testEpic2.setTaskName(TEST_TASK_NAME);
        Stubs.testEpic2.setStatus(TEST_TASK_STATUS);

        //Then
        assertEquals(Stubs.testEpic2, Stubs.testEpic1,
                "Задачи не равны");
    }

    @DisplayName("Подзадача корректно добавляется к Epic")
    @Test
    void test_set_Subtask_For_Epic_Correctly_Test() {
        //Given
        Subtask TEST_SUBTASK = new Subtask(
                4, "testName3", "TestInfo3", 3);

        //When
        Stubs.testEpic3.setSubtaskForEpic(TEST_SUBTASK.getTaskId(), TEST_SUBTASK);

        //Then
        assertNotNull(Stubs.testEpic3.getSubtaskForEpic(), "Подзадача не добавлена к Epic");
    }

}