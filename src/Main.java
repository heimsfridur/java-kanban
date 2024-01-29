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

        Task task1 = new Task("solve the problem", "It's complicated", Status.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("feed the cat", "He's hungry", Status.NEW);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Moving", "Pack all your things and leave the country.");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Pack boxes", "pack all boxes in the world", 2, Status.DONE);
        taskManager.createSubtask(subtask1);

        ArrayList<Task> allTasks = taskManager.getAllTasks();
        ArrayList<Subtask> allSubtasks = taskManager.getAllSubtasks();
        ArrayList<Epic> allEpics = taskManager.getAllEpics();

    }
}
