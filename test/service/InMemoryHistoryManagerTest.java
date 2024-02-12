package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    public void shouldAddTaskToHistory() {
        InMemoryHistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
        Task task = new Task("task1 name", "task1 descr", Status.NEW);

        inMemoryHistoryManager.add(task);

        ArrayList<Task> savedTasks = inMemoryHistoryManager.getHistory();
        assertNotNull(savedTasks, "History is empty.");
        assertEquals(1, savedTasks.size(), "The size of the history is not correct");
    }

}