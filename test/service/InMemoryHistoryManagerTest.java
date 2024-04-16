package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager inMemoryHistoryManager;
    TaskManager taskManager;
    Task task0;
    Task task1;
    Task task2;

    @BeforeEach
    public void beforeEach() {
        inMemoryHistoryManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
        task0 = new Task("task0", "task0_descr", Status.NEW, 0,
                LocalDateTime.of(2024, 4, 16, 12, 30), Duration.ofMinutes(30));
        task1 = new Task("task1", "task1_descr", Status.NEW, 1,
                LocalDateTime.of(2024, 4, 28, 2, 30), Duration.ofMinutes(20));
        task2 = new Task("task2", "task2_descr", Status.DONE, 2,
                LocalDateTime.of(2024, 4, 30, 12, 30), Duration.ofMinutes(30));
    }

    @Test
    public void shouldAddTaskToHistory() {
        Task task = new Task("task1 name", "task1 descr", Status.NEW);

        inMemoryHistoryManager.add(task);

        List<Task> savedTasks = inMemoryHistoryManager.getHistory();
        assertNotNull(savedTasks, "History is empty.");
        assertEquals(1, savedTasks.size(), "The size of the history is not correct");
    }

    @Test
    public void shouldNotAddNullTask() {
        Task nullTask = taskManager.getTaskById(100);
        inMemoryHistoryManager.add(nullTask);
        assertEquals(inMemoryHistoryManager.getHistory().size(), 0, "The null task was added.");
    }

    @Test
    public void shouldReturnEmptyHistoryIfNothingWasAdded() {
        taskManager.createTask(new Task("task1", "task1_descr", Status.NEW,
                LocalDateTime.of(2024, 4, 16, 12, 30), Duration.ofMinutes(30)));

        assertEquals(inMemoryHistoryManager.getHistory().size(), 0, "History is not empty, but should be");
    }

    @Test
    public void shouldNotDuplicateTasksInHistory() {
        inMemoryHistoryManager.add(task0);
        inMemoryHistoryManager.add(task0);

        assertEquals(inMemoryHistoryManager.getHistory().size(), 1, "Tasks were duplicated in history");
    }

    private List<Task> createHistory() {
        taskManager.createTask(task0);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        inMemoryHistoryManager.add(task0);
        inMemoryHistoryManager.add(task1);
        inMemoryHistoryManager.add(task2);

        return inMemoryHistoryManager.getHistory();
    }

    @Test
    public void shouldDeleteTaskFromTheBeginningOfHistory() {
        createHistory();

        inMemoryHistoryManager.remove(0);

        List<Task> newHistory = inMemoryHistoryManager.getHistory();

        boolean correctOrder =  newHistory.indexOf(task1) == 0 && newHistory.indexOf(task2) == 1;
        assertEquals(newHistory.size(), 2, "Task was not deleted.");
        assertTrue(correctOrder, "The order of elements is incorrect.");
    }

    @Test
    public void shouldDeleteTaskFromTheEndOfHistory() {
        createHistory();

        inMemoryHistoryManager.remove(2);

        List<Task> newHistory = inMemoryHistoryManager.getHistory();

        boolean correctOrder =  newHistory.indexOf(task0) == 0 && newHistory.indexOf(task1) == 1;
        assertEquals(newHistory.size(), 2, "Task was not deleted.");
        assertTrue(correctOrder, "The order of elements is incorrect.");
    }

    @Test
    public void shouldDeleteTaskFromTheMiddleOfHistory() {
        createHistory();

        inMemoryHistoryManager.remove(1);

        List<Task> newHistory = inMemoryHistoryManager.getHistory();

        boolean correctOrder =  newHistory.indexOf(task0) == 0 && newHistory.indexOf(task2) == 1;
        assertEquals(newHistory.size(), 2, "Task was not deleted.");
        assertTrue(correctOrder, "The order of elements is incorrect.");
    }
}