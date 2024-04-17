package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.regex.Pattern;

public class TasksHandler implements HttpHandler {
    private TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException  {
        String response = "";
        int statusCode = 200;

        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    break;
                case "POST":
                    break;
                case "DELETE":
                    if (Pattern.matches("^/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/", "");
                        int id = parsePathId(pathId);
                        if (id == -1) {
                            statusCode = 405;
                            response = "The ID " + pathId + " for deletion is incorrect.";
                        } else {
                            taskManager.removeTaskById(id);
                            response = "Task with ID " + id + " was deleted.";
                        }
                    }
                    break;
                default: {
                    statusCode = 405;
                    response = "There is no such endpoint.";
                    break;
                }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        try (OutputStream os = httpExchange.getResponseBody()) {
            httpExchange.sendResponseHeaders(statusCode, 0);
            os.write(response.getBytes());
        }
    }

    private int parsePathId(String pathId) {
        try {
            return Integer.parseInt(pathId);
        } catch (NumberFormatException exc) {
            return -1;
        }
    }
}
