package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Subtask;
import model.Task;
import service.TaskManager;
import static server.HttpTaskServer.gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.regex.Pattern;

import static server.HttpTaskServer.gson;

public class SubtasksHandler implements HttpHandler {
    private TaskManager taskManager;
    private String response;
    private int statusCode;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.response = "";
        this.statusCode = 200;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException  {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    handleGetSubtaskMethod(path);
                    break;
                case "POST":

                    break;
                case "DELETE":
                    handleDeleteTaskMethod(path);
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

    private void handleDeleteTaskMethod(String path) {
        if (Pattern.matches("^/subtasks/\\d+$", path)) {
            String pathId = path.replaceFirst("/subtasks/", "");
            int id = parsePathId(pathId);
            if (id == -1) {
                statusCode = 405;
                response = "The ID " + pathId + " for deletion is incorrect.";
            } else {
                taskManager.removeSubtaskById(id);
                response = "Subtask with ID " + id + " was deleted.";
            }
        }
    }

        private void handleGetSubtaskMethod(String path) {

        if (Pattern.matches("^/subtasks$", path)) {
            response = gson.toJson(taskManager.getAllSubtasks());

        } else if (Pattern.matches("^/subtasks/\\d+$", path)) {
            String pathId = path.replaceFirst("/subtasks/", "");
            int id = parsePathId(pathId);
            if (id == -1) {
                statusCode = 405;
                response = "ID " + pathId + " is incorrect";
            } else {
                Subtask subtask = taskManager.getSubtaskById(id);
                if (subtask != null) {
                    response = gson.toJson(subtask);
                } else {
                    statusCode = 404;
                    response = "Subtask with ID " + id + " is not found.";
                }
            }
        } else {
            statusCode = 405;
            response = "There is no such endpoint for GET method.";
        }
    }
}
