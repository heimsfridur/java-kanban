package service;

import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerEpicStatusTest {

    @Test
    public void shouldSetDoneStatusToEpicWhenAllSubtasksAreDone() {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = new Epic("epic1 name", "epic1 descr");
        taskManager.createEpic(epic1);
        int epic1Id = epic1.getId();

        Subtask subtask1 = new Subtask("subtask1Epic1", "subtask1Epic1_descr", epic1Id, Status.DONE,
                LocalDateTime.of(2024, 4, 16, 12, 30), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("subtask2Epic1", "subtask2Epic1", epic1Id, Status.DONE,
                LocalDateTime.of(2024, 4, 28, 2, 30), Duration.ofMinutes(20));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(epic1.getStatus(), Status.DONE, "Epic should be in status DONE, but it's not.");
    }

    @Test
    public void shouldSetNewStatusToEpicWhenAllSubtasksAreNew() {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = new Epic("epic1 name", "epic1 descr");
        taskManager.createEpic(epic1);
        int epic1Id = epic1.getId();

        Subtask subtask1 = new Subtask("subtask1Epic1", "subtask1Epic1_descr", epic1Id, Status.NEW,
                LocalDateTime.of(2024, 4, 16, 12, 30), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("subtask2Epic1", "subtask2Epic1", epic1Id, Status.NEW,
                LocalDateTime.of(2024, 4, 28, 2, 30), Duration.ofMinutes(20));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(epic1.getStatus(), Status.NEW, "Epic should be in status DONE, but it's not.");
    }

    @Test
    public void shouldSetInProgressStatusToEpicWhenSubtasksAreNewAndDone() {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = new Epic("epic1 name", "epic1 descr");
        taskManager.createEpic(epic1);
        int epic1Id = epic1.getId();

        Subtask subtask1 = new Subtask("subtask1Epic1", "subtask1Epic1_descr", epic1Id, Status.NEW,
                LocalDateTime.of(2024, 4, 16, 12, 30), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("subtask2Epic1", "subtask2Epic1", epic1Id, Status.DONE,
                LocalDateTime.of(2024, 4, 28, 2, 30), Duration.ofMinutes(20));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(epic1.getStatus(), Status.IN_PROGRESS, "Epic should be in status DONE, but it's not.");
    }

    @Test
    public void shouldSetInProgressStatusToEpicWhenSubtasksAreInProgress() {
        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = new Epic("epic1 name", "epic1 descr");
        taskManager.createEpic(epic1);
        int epic1Id = epic1.getId();

        Subtask subtask1 = new Subtask("subtask1Epic1", "subtask1Epic1_descr", epic1Id,
                Status.IN_PROGRESS, LocalDateTime.of(2024, 4, 16, 12, 30),
                Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("subtask2Epic1", "subtask2Epic1", epic1Id, Status.IN_PROGRESS,
                LocalDateTime.of(2024, 4, 28, 2, 30), Duration.ofMinutes(20));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(epic1.getStatus(), Status.IN_PROGRESS, "Epic should be in status DONE, but it's not.");
    }
}
