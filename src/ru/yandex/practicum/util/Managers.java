package ru.yandex.practicum.util;

import ru.yandex.practicum.exceptions.ManagerSaveException;
import ru.yandex.practicum.manager.impl.FileBackedTaskManager;
import ru.yandex.practicum.manager.impl.InMemoryHistoryManager;
import ru.yandex.practicum.manager.impl.InMemoryTaskManager;

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

    private Managers() {
    }

    public static InMemoryTaskManager getDefault() {
        return FileBackedTaskManager.loadFromFile(SAVE_FILE);
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return historyManager;
    }

}