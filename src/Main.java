import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Решить задачку", "Решить сложную задачку по физкультуре.", Status.NEW);
        taskManager.createTask(task1);

        Epic epic1 = new Epic("Переезд", "организовать большой сбор и переехать");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Собрать коробки", "собрать все коробки мира", 1, Status.DONE);
        taskManager.createSubtask(subtask1);
    }
}
