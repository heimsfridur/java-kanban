import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Let's start!");
        TaskManager inMemoryTaskManager = Managers.getDefault();

        // 0
        Task task1 = new Task("solve the problem", "It's complicated", Status.NEW);
        inMemoryTaskManager.createTask(task1);
        //1
        Task task2 = new Task("feed the cat", "He's hungry", Status.NEW);
        inMemoryTaskManager.createTask(task2);

        //2
        Epic epic1 = new Epic("Moving", "Pack all your things and leave the country.");
        inMemoryTaskManager.createEpic(epic1);
        int epic1Id = epic1.getId();

        //3
        Subtask subtask1Epic1 = new Subtask("Pack boxes", "pack all boxes in the world", epic1Id, Status.DONE);
        inMemoryTaskManager.createSubtask(subtask1Epic1);

        //4
        Subtask subtask2Epic1 = new Subtask("Pack socks", "there are too many socks", epic1Id, Status.NEW);
        inMemoryTaskManager.createSubtask(subtask2Epic1);

        //5
        Epic epic2 = new Epic("Learn how to cook", "It must be tasty and healthy.");
        inMemoryTaskManager.createEpic(epic2);
        int epic2Id = epic2.getId();
        //6
        Subtask subtask1Epic2 = new Subtask("Read the cookbook", "but it is boring", epic2Id, Status.IN_PROGRESS);
        inMemoryTaskManager.createSubtask(subtask1Epic2);

        ArrayList<Task> allTasks = inMemoryTaskManager.getAllTasks();
        ArrayList<Subtask> allSubtasks = inMemoryTaskManager.getAllSubtasks();
        ArrayList<Epic> allEpics = inMemoryTaskManager.getAllEpics();

        Task taskById = inMemoryTaskManager.getTaskById(1);
        Subtask subtaskById = inMemoryTaskManager.getSubtaskById(4);
        Epic epicById = inMemoryTaskManager.getEpicById(2);
    //    inMemoryTaskManager.getTaskById(0);
        List<Task> history = inMemoryTaskManager.getHistory();
       // Epic epicFromHistory = (Epic) history.get(2);
       // ArrayList<Integer> list = epicFromHistory.getSubtasksIds();


        ArrayList<Subtask> subtasksFromEpic = inMemoryTaskManager.getSubtasksFromEpic(epic1);

        Task newTask = new Task("Feed the DOG", "it was not a CAT", Status.DONE, 1);
        inMemoryTaskManager.updateTask(newTask);

        Subtask newSubtask = new Subtask("Pack socks", "there are too many socks, but I did it",
                2, Status.DONE, 4);
        inMemoryTaskManager.updateSubtask(newSubtask);

//        taskManager.removeTaskById(0);
//        taskManager.removeSubtaskById(6);
//        taskManager.removeEpicById(2);

        inMemoryTaskManager.removeAllTasks();
//        taskManager.removeAllSubtasks();
        inMemoryTaskManager.removeAllEpics();
    }
}
