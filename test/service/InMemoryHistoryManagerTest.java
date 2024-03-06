package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    public void shouldAddTaskToHistory() {
        HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
        Task task = new Task("task1 name", "task1 descr", Status.NEW);

        inMemoryHistoryManager.add(task);

        List<Task> savedTasks = inMemoryHistoryManager.getHistory();
        assertNotNull(savedTasks, "History is empty.");
        assertEquals(1, savedTasks.size(), "The size of the history is not correct");
    }

    @Test
    public void shouldNotAddNullTask() {
        HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

        TaskManager taskManager = Managers.getDefault();
        Task nullTask = taskManager.getTaskById(100);
        inMemoryHistoryManager.add(nullTask);
        assertEquals(inMemoryHistoryManager.getHistory().size(), 0, "The null task was added.");
    }
}