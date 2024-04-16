package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static service.FileBackedTaskManager.loadFromFile;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File tempFile;
    @BeforeEach
    public void beforeEachTest() throws IOException {
        tempFile = File.createTempFile("temp", ".csv");
        taskManager = new FileBackedTaskManager(tempFile);
        super.beforeEach();
    }

    @Test
    public void shouldLoadFromFile() {
        FileBackedTaskManager newManager = loadFromFile(tempFile);
        assertEquals(newManager.getAllTasks(), taskManager.getAllTasks(),
                "tasks in managers are not the same");
    }

}
