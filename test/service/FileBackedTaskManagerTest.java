package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static service.FileBackedTaskManager.loadFromFile;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager fileBackedTaskManager;
    File tempFile;
    @BeforeEach
    public void beforeEach() throws IOException {
        tempFile = File.createTempFile("temp", ".csv");
        this.fileBackedTaskManager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("task1", "task1_descr", Status.NEW);
        fileBackedTaskManager.createTask(task1);

        Epic epic1 = new Epic("epic1", "epic1_descr");
        fileBackedTaskManager.createEpic(epic1);
        int epic1Id = epic1.getId();

        Subtask subtask1Epic1 = new Subtask("subtask1Epic1", "subtask1Epic1_descr", epic1Id, Status.DONE);
        fileBackedTaskManager.createSubtask(subtask1Epic1);
    }

    @Test
    public void shouldSaveTaskSubtaskEpic() {
        ArrayList<Task> tasks = fileBackedTaskManager.getAllTasks();
        assertEquals(tasks.size(), 1, "the number of tasks is incorrect");

        ArrayList<Subtask> subtasks = fileBackedTaskManager.getAllSubtasks();
        assertEquals(subtasks.size(), 1, "the number of subtasks is incorrect");

        ArrayList<Epic> epics = fileBackedTaskManager.getAllEpics();
        assertEquals(epics.size(), 1, "the number of epics is incorrect");
    }

    @Test
    public void shouldLoadFromFile() {
        FileBackedTaskManager newManager = loadFromFile(tempFile);
        assertEquals(newManager.getAllTasks(), fileBackedTaskManager.getAllTasks(),
                "tasks in managers are not the same");
    }
}
