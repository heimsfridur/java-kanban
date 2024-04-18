package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.TaskOverlappingException;
import model.Task;
import server.adapters.DurationAdapter;
import static server.HttpTaskServer.gson;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Pattern;

public class TasksHandler implements HttpHandler {
    private TaskManager taskManager;
    private String response;
    private int statusCode;

    public TasksHandler(TaskManager taskManager) {
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
                    handleGetTaskMethod(path);
                    break;
                case "POST":
                    handlePostTaskMethod(httpExchange);
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
            statusCode = 405;
            response = "Something went wrong. Please check your url and task id.";
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
        } else {
            statusCode = 405;
            response = "There is no such endpoint for DELETE method.";
        }
    }

    private void handleGetTaskMethod(String path) {
        if (Pattern.matches("^/tasks$", path)) {
            response = gson.toJson(taskManager.getAllTasks());
        } else if (Pattern.matches("^/tasks/\\d+$", path)) {
            String pathId = path.replaceFirst("/tasks/", "");
            int id = parsePathId(pathId);
            if (id == -1) {
                statusCode = 405;
                response = "ID " + pathId + " is incorrect";
            } else {
                Task task = taskManager.getTaskById(id);
                if (task != null) {
                    response = gson.toJson(task);
                } else {
                    statusCode = 404;
                    response = "Task with ID " + id + " is not found.";
                }
            }
        } else {
            statusCode = 405;
            response = "There is no such endpoint for GET method.";
        }
    }

    public void handlePostTaskMethod(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();

        if (path.split("/").length == 2) {
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            if (body.isEmpty()) {
                statusCode = 400;
                response = "Body request is empty.";
            } else {
                try {
                    JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                    Task task = gson.fromJson(body, Task.class);

                    if (jsonObject.has("id") && task.getId() != -1) {
                        int taskId = task.getId();
                        taskManager.updateTask(task);
                        response = "Task with ID " + taskId + " was updated.";
                    } else {
                        taskManager.createTask(task);
                        response = "Task was created.";
                    }
                    statusCode = 201;

                } catch (TaskOverlappingException exc) {
                    statusCode = 406;
                    response = "Can't create or update task. It overlaps with another task.";
                }
            }
        } else {
            statusCode = 405;
            response = "There is no such endpoint for POST method.";
        }
    }
}
