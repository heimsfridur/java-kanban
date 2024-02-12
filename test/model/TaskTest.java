package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    public void tasksWithSameIdsShouldBeEqual() {
        Task task1 = new Task("wash the dishes", "the dishes are dirty", Status.NEW);
        Task task2 = new Task("wash the dishes", "the dishes are dirty", Status.DONE);
        task1.setId(0);
        task2.setId(0);
        assertEquals(task1, task2, "Tasks are not the same");
    }
}