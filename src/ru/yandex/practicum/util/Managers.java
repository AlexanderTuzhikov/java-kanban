package ru.yandex.practicum.util;

import ru.yandex.practicum.manager.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Managers {
    private static final Path SAVE_FILE = createTempFile();

    private static Path createTempFile() {
        Path tempFile;
        try {
            tempFile = Files.createTempFile("save file", ".csv");
            System.out.println("Файл успешно создан: " + tempFile);
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка создания временного файла", exception);
        }
        return tempFile;
    }

    private static final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    private static final InMemoryTaskManager taskManager = new InMemoryTaskManager();

    private Managers() {
    }

    public static InMemoryTaskManager getDefault() {
        return taskManager;
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return historyManager;
    }

    public static FileBackedTaskManager getDefaultFileBacked() {
        return FileBackedTaskManager.loadFromFile(SAVE_FILE);
    }
}