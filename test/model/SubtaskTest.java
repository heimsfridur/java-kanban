package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    public void subtasksWithSameIdsShouldBeEqual() {
        Subtask subtask1 = new Subtask("Pack boxes", "pack all boxes in the world", 100, Status.NEW);
        Subtask subtask2 = new Subtask("Pack boxes", "pack all boxes in the world", 100, Status.DONE);
        subtask1.setId(0);
        subtask2.setId(0);
        assertEquals(subtask1, subtask2, "Subtasks are not the same");
    }

    @Test
    public void impossibleToMakeSubtaskEpicForItself() {
        Subtask subtask = new Subtask("subName", "sub description", 2, Status.DONE);

        assertThrows(IllegalArgumentException.class, () -> {
            subtask.setId(2);
        });
    }
}