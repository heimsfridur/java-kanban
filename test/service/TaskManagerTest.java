package service;

import exceptions.TaskOverlappingException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager>{
    protected T taskManager;
    protected Task task1;
    protected Task task2;
    protected Subtask subtask1Epic1;
    protected Subtask subtask2Epic1;
    protected Epic epic1;

    public void beforeEach() {
        // 0
        task1 = new Task("task1", "task1_descr", Status.NEW,
                LocalDateTime.of(2024, 4, 16, 12, 30), Duration.ofMinutes(30));
        taskManager.createTask(task1);
        //1
        task2 = new Task("task2", "task2_descr", Status.NEW,
                LocalDateTime.of(2024, 4, 28, 2, 30), Duration.ofMinutes(20));
        taskManager.createTask(task2);
        //2
        epic1 = new Epic("epic1", "epic1_descr");
        taskManager.createEpic(epic1);
        int epic1Id = epic1.getId();
        //3
        subtask1Epic1 = new Subtask("subtask1Epic1", "subtask1Epic1_descr", epic1Id, Status.DONE,
                LocalDateTime.of(2024, 4, 10, 15, 0), Duration.ofMinutes(5));
        taskManager.createSubtask(subtask1Epic1);
        //4
        subtask2Epic1 = new Subtask("subtask2Epic1", "subtask2Epic1_descr", epic1Id, Status.NEW,
                LocalDateTime.of(2024, 4, 16, 19, 30), Duration.ofMinutes(120));
        taskManager.createSubtask(subtask2Epic1);
    }

    @Test
    public void shouldCreateTasks() {
        Task savedTask = taskManager.getTaskById(task1.getId());

        assertNotNull(savedTask, "Can't find a task.");
        assertEquals(task1, savedTask, "Tasks are not equal");
    }
    @Test
    public void shouldCreateSubtasks() {
        Task savedSubtask = taskManager.getSubtaskById(subtask1Epic1.getId());

        assertNotNull(savedSubtask, "Can't find a subtask.");
        assertEquals(subtask1Epic1, savedSubtask, "Tasks are not equal");
    }

    @Test
    public void shouldCreateEpic() {
        Task savedEpic = taskManager.getEpicById(epic1.getId());

        assertNotNull(savedEpic, "Can't find an epic.");
        assertEquals(epic1, savedEpic, "Tasks are not equal");
    }

    @Test
    public void shouldGetAllTasks() {
        ArrayList<Task> allTasks = taskManager.getAllTasks();

        assertEquals(allTasks.size(), 2, "The number of tasks is incorrect.");
        assertEquals(allTasks.getFirst(), task1, "the first task is incorrect.");
    }

    @Test
    public void shouldGetAllSubtasks() {
        ArrayList<Subtask> allSubtasks = taskManager.getAllSubtasks();

        assertEquals(allSubtasks.size(), 2, "The number of subtasks is incorrect.");
        assertEquals(allSubtasks.getFirst(), subtask1Epic1, "the first subtask is incorrect.");
    }

    @Test
    public void shouldGetAllEpics() {
        ArrayList<Epic> allEpics = taskManager.getAllEpics();

        assertEquals(allEpics.size(), 1, "The number of subtasks is incorrect.");
        assertEquals(allEpics.getFirst(), epic1, "the first epic is incorrect.");
    }

    @Test
    public void shouldRemoveAllTasks() {
        taskManager.removeAllTasks();

        assertEquals(taskManager.getAllTasks().size(), 0, "Tasks were not deleted.");
    }

    @Test
    public void shouldRemoveAllSubtasks() {
        taskManager.removeAllSubtasks();

        assertEquals(taskManager.getAllSubtasks().size(), 0, "Subtasks were not deleted.");
    }

    @Test
    public void shouldRemoveAllEpics() {
        taskManager.removeAllEpics();

        assertEquals(taskManager.getAllEpics().size(), 0, "Epics were not deleted.");
        assertEquals(taskManager.getAllSubtasks().size(), 0, "Subtasks from epics were not deleted.");
    }

    @Test
    public void shouldRemoveTaskById() {
        int task1Id = task1.getId();
        taskManager.removeTaskById(task1Id);
        assertFalse(taskManager.getAllTasks().contains(task1), "Task was not deleted by id.");
    }

    @Test
    public void shouldRemoveSubtaskById() {
        int subtask1Id = subtask1Epic1.getId();
        taskManager.removeSubtaskById(subtask1Id);
        assertFalse(taskManager.getAllSubtasks().contains(subtask1Id), "Subtask was not deleted by id.");
    }

    @Test
    public void shouldRemoveEpicById() {
        int epic1Id = epic1.getId();
        taskManager.removeEpicById(epic1Id);
        assertFalse(taskManager.getAllEpics().contains(epic1), "Epic was not deleted by id.");
        assertFalse(taskManager.getAllSubtasks().contains(subtask1Epic1), "Subtask was not deleted with epic.");
    }

    @Test
    public void shouldGetSubtasksFromEpic() {
        ArrayList<Subtask> subtasksFromEpic = taskManager.getSubtasksFromEpic(epic1);

        ArrayList<Subtask> listForCheck = new ArrayList<>();
        listForCheck.add(subtask1Epic1);
        listForCheck.add(subtask2Epic1);

        assertEquals(subtasksFromEpic, listForCheck, "Subtasks from epic are not correct.");
    }

    @Test
    public void shouldGetTaskById() {
        Task returnedTask = taskManager.getTaskById(task1.getId());
        assertEquals(returnedTask, task1, "Task is incorrect.");
    }

    @Test
    public void shouldGetSubtaskById() {
        Task returnedSubtask = taskManager.getSubtaskById(subtask1Epic1.getId());
        assertEquals(returnedSubtask, subtask1Epic1, "Subtask is incorrect.");
    }

    @Test
    public void shouldGetEpicById() {
        Task returnedEpic = taskManager.getEpicById(epic1.getId());
        assertEquals(returnedEpic, epic1, "Epic is incorrect.");
    }

    @Test
    public void shouldUpdateTask() {
        int task1Id = task1.getId();
        Task oldTask = taskManager.getTaskById(task1Id);
        oldTask.setName("EVERYTHING IS DONE, GO HOME.");
        taskManager.updateTask(oldTask);

        Task newTask = taskManager.getTaskById(task1Id);

        assertEquals(oldTask.getName(), newTask.getName(), "Task was not updated.");
    }

    @Test
    public void shouldUpdateSubtask() {
        int subtask1Id = subtask1Epic1.getId();
        Subtask oldSubtask = taskManager.getSubtaskById(subtask1Id);
        oldSubtask.setStatus(Status.NEW);
        taskManager.updateSubtask(oldSubtask);

        Subtask newSubtask = taskManager.getSubtaskById(subtask1Id);

        assertEquals(oldSubtask.getStatus(), newSubtask.getStatus(), "Subtask was not updated.");
        assertEquals(Status.NEW, epic1.getStatus(), "Epic status was not updated.");
    }

    @Test
    public void shouldUpdateEpic() {
        int epic1Id = epic1.getId();
        Epic oldEpic = taskManager.getEpicById(epic1Id);
        oldEpic.setName("EVERYTHING IS DONE, GO HOME");
        taskManager.updateEpic(oldEpic);

        Epic newEpic = taskManager.getEpicById(epic1Id);

        assertEquals(oldEpic.getName(), newEpic.getName(), "Epic was not updated.");
    }

    @Test
    public void shouldGetHistory() {
        int task1Id = task1.getId();
        int epic1Id = epic1.getId();
        int subtask1Id = subtask1Epic1.getId();

        taskManager.getTaskById(task1Id);
        taskManager.getEpicById(epic1Id);
        taskManager.getSubtaskById(subtask1Id);


        List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size(), "Number of tasks in history is incorrect.");

        taskManager.removeTaskById(task1Id);
        taskManager.removeEpicById(epic1Id);
        history = taskManager.getHistory();
        assertEquals(0, history.size(), "Tasks were not deleted.");
    }

    @Test
    public void shouldGetPrioritizedTasks() {
        assertEquals(4, taskManager.getPrioritizedTasks().size(),
                "Number of tasks with start date is incorrect.");
    }

    @Test
    public void shouldNotCreateOverlappingSubtasks() {
        Subtask overlappingSubtask = new Subtask("subtaskOverlap", "subtaskOverlap_descr", epic1.getId(),
                Status.IN_PROGRESS, LocalDateTime.of(2024, 4, 16, 12, 0),
                Duration.ofMinutes(60));

        assertThrows(TaskOverlappingException.class, () -> {
            taskManager.createSubtask(overlappingSubtask);
        });
    }

    @Test
    public void shouldNotCreateOverlappingTasks() {
        Task overlappingTask = new Task("task", "taskOverlap_descr",
                Status.IN_PROGRESS, LocalDateTime.of(2024, 4, 16, 12, 0),
                Duration.ofMinutes(60));

        assertThrows(TaskOverlappingException.class, () -> {
            taskManager.createTask(overlappingTask);
        });
    }
}
