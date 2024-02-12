package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    public void taskManagerIsNotNull() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "taskManager is null");
    }

    @Test
    public void inMemoryHistoryManagerIsNotNull() {
        InMemoryHistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
        assertNotNull(inMemoryHistoryManager, "inMemoryManager is null");
    }
}