package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    public void shouldCreateTasks() {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("task1 name", "task1 descr", Status.NEW);
        taskManager.createTask(task1);

        Task savedTask = taskManager.getTaskById(task1.getId());

        assertNotNull(savedTask, "Can't find a task.");
        assertEquals(task1, savedTask, "Tasks are not equal");

        taskManager.createTask(new Task("task2 name", "task2 descr", Status.IN_PROGRESS));

        ArrayList<Task> tasksList = taskManager.getAllTasks();

        assertNotNull(tasksList, "Can not return tasks.");
        assertEquals(2, tasksList.size(), "Invalid number of tasks.");
        assertEquals(task1, tasksList.get(0), "Tasks are not the same.");
    }

    @Test
    public void shouldCreateEpic() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("epic1 name", "epic1 descr");
        taskManager.createEpic(epic1);
        int epic1Id = epic1.getId();

        Epic savedEpic = taskManager.getEpicById(epic1Id);
        assertNotNull(savedEpic, "Can't find epic.");
        assertEquals(epic1, savedEpic, "Epics are not equal");

        taskManager.createEpic(new Epic("Learn how to cook", "It must be tasty and healthy."));
        ArrayList<Epic> epicList = taskManager.getAllEpics();

        assertNotNull(epicList, "Can not return epics.");
        assertEquals(2, epicList.size(), "Invalid number of epics.");
        assertEquals(epic1, epicList.get(0), "Epics are not the same.");
    }

    @Test
    public void shouldCreateSubtasks() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("epic1 name", "epic1 descr");
        taskManager.createEpic(epic1);
        int epic1Id = epic1.getId();

        Subtask subtask1Epic1 = new Subtask("subtask1 name", "subtask1 descr", epic1Id, Status.DONE);
        taskManager.createSubtask(subtask1Epic1);

        Subtask savedSubtask = taskManager.getSubtaskById(subtask1Epic1.getId());
        assertNotNull(savedSubtask, "Can't find subtask.");
        assertEquals(subtask1Epic1, savedSubtask, "Subtasks are not equal");

        Subtask subtask2Epic1 = new Subtask("subtask2 name", "subtask2 descr", epic1Id, Status.NEW);
        taskManager.createSubtask(subtask2Epic1);

        ArrayList<Subtask> subtasksList = taskManager.getAllSubtasks();

        assertNotNull(subtasksList, "Can not return subtasks.");
        assertEquals(2, subtasksList.size(), "Invalid number of subtasks.");
        assertEquals(subtask1Epic1, subtasksList.get(0), "Subtasks are not the same.");
    }

    @Test
    public void shouldRemoveTask() {
        TaskManager taskManager = Managers.getDefault();

        Task task = new Task("task name", "task descr", Status.NEW);
        taskManager.createTask(task);
        taskManager.removeTaskById(task.getId());
        ArrayList<Task> tasks = taskManager.getAllTasks();
        assertEquals(0, tasks.size(), "The task was not deleted.");
    }

    @Test
    public void shouldRemoveEpic() {
        TaskManager taskManager = Managers.getDefault();

        Epic epic = new Epic("epic name", "epic descr");
        taskManager.createEpic(epic);
        taskManager.removeEpicById(epic.getId());
        ArrayList<Epic> epics = taskManager.getAllEpics();
        assertEquals(0, epics.size(), "The epic was not deleted.");
    }

    @Test
    public void shouldRemoveSubtask() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("epic1 name", "epic1 descr");
        taskManager.createEpic(epic1);
        int epic1Id = epic1.getId();

        Subtask subtask = new Subtask("subtask1 name", "subtask1 descr", epic1Id, Status.DONE);
        taskManager.createSubtask(subtask);
        taskManager.removeSubtaskById(subtask.getId());
        ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();
        assertEquals(0, subtasks.size(), "The subtask was not deleted.");
    }

    @Test
    public void shouldUpdateEpic() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("epic1 name", "epic1 descr");
        taskManager.createEpic(epic1);
        int epic1Id = epic1.getId();
        taskManager.createSubtask(new Subtask("subtask1 name", "subtask1 descr", epic1Id, Status.DONE));

        Epic epic2 = new Epic("epic1 new Name", "epic1 new Descr", epic1Id);
        taskManager.updateEpic(epic2);

        Epic savedEpic = taskManager.getEpicById(epic1Id);
        assertEquals(savedEpic.getName(), epic2.getName(), "Epic name was not updated.");
        assertEquals(savedEpic.getDescription(), epic2.getDescription(), "Epic description was not updated.");
        assertEquals(epic2, savedEpic, "Epic was not updated.");
    }
}