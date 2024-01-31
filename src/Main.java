import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.TaskManager;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Let's start!");
        TaskManager taskManager = new TaskManager();

        // 0
        Task task1 = new Task("solve the problem", "It's complicated", Status.NEW);
        taskManager.createTask(task1);
        //1
        Task task2 = new Task("feed the cat", "He's hungry", Status.NEW);
        taskManager.createTask(task2);

        //2
        Epic epic1 = new Epic("Moving", "Pack all your things and leave the country.");
        taskManager.createEpic(epic1);
        int epic1Id = epic1.getId();

        //3
        Subtask subtask1Epic1 = new Subtask("Pack boxes", "pack all boxes in the world", epic1Id, Status.DONE);
        taskManager.createSubtask(subtask1Epic1);

        //4
        Subtask subtask2Epic1 = new Subtask("Pack socks", "there are too many socks", epic1Id, Status.NEW);
        taskManager.createSubtask(subtask2Epic1);

        //5
        Epic epic2 = new Epic("Learn how to cook", "It must be tasty and healthy.");
        taskManager.createEpic(epic2);
        int epic2Id = epic2.getId();
        //6
        Subtask subtask1Epic2 = new Subtask("Read the cookbook", "but it is boring", epic2Id, Status.IN_PROGRESS);
        taskManager.createSubtask(subtask1Epic2);

        ArrayList<Task> allTasks = taskManager.getAllTasks();
        ArrayList<Subtask> allSubtasks = taskManager.getAllSubtasks();
        ArrayList<Epic> allEpics = taskManager.getAllEpics();

        Task taskById = taskManager.getTaskById(1);
        Subtask subtaskById = taskManager.getSubtaskById(4);
        Epic epicById = taskManager.getEpicById(2);

        ArrayList<Subtask> subtasksFromEpic = taskManager.getSubtasksFromEpic(epic1);

        Task newTask = new Task("Feed the DOG", "it was not a CAT", Status.DONE, 1);
        taskManager.updateTask(newTask);

        Subtask newSubtask = new Subtask("Pack socks", "there are too many socks, but I did it",
                2, Status.DONE, 4);
        taskManager.updateSubtask(newSubtask);

//        taskManager.removeTaskById(0);
//        taskManager.removeSubtaskById(6);
//        taskManager.removeEpicById(2);

        taskManager.removeAllTasks();
//        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
    }
}
