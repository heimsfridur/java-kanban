package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private HttpServer httpServer;
    private static final int PORT = 8080;
    protected TaskManager taskManager;

    protected static Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.gson = Managers.getGson();
    }

    public HttpTaskServer() throws IOException {
        this.taskManager = Managers.getDefault();
        this.gson = Managers.getGson();
    }


    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();

        //0
        Task task1 = new Task("task1", "task1_descr", Status.NEW,
                LocalDateTime.of(2024, 4, 16, 12, 30), Duration.ofMinutes(30));
        taskManager.createTask(task1);
        //1
        Task task2 = new Task("task2", "task2_descr", Status.NEW,
                LocalDateTime.of(2024, 4, 28, 2, 30), Duration.ofMinutes(20));
        taskManager.createTask(task2);
        //2
        Epic epic1 = new Epic("epic1", "epic1_descr");
        taskManager.createEpic(epic1);
        int epic1Id = epic1.getId();
        //3
        Subtask subtask1Epic1 = new Subtask("subtask1Epic1", "subtask1Epic1_descr", epic1Id, Status.DONE,
                LocalDateTime.of(2024, 4, 10, 15, 0), Duration.ofMinutes(5));
        taskManager.createSubtask(subtask1Epic1);
        //4
        Subtask subtask2Epic1 = new Subtask("subtask2Epic1", "subtask2Epic1_descr", epic1Id, Status.NEW,
                LocalDateTime.of(2024, 4, 16, 19, 30), Duration.ofMinutes(120));
        taskManager.createSubtask(subtask2Epic1);
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PriorityHandler(taskManager));

        httpServer.start();
        System.out.println("Server is running on port: " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Server is stopped on port: " + PORT);
    }
}
