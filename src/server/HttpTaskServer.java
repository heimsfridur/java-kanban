package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private HttpServer httpServer;
    private static final int PORT = 8080;
    protected TaskManager taskManager;

    Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
    }

    public HttpTaskServer() throws IOException {
        this.taskManager = Managers.getDefault();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));


        httpServer.start();
        System.out.println("Server is running on port: " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Server is stopped on port: " + PORT);
    }
}
