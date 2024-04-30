package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.TaskOverlappingException;
import model.Subtask;
import service.TaskManager;
import static server.HttpTaskServer.gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SubtasksHandler implements HttpHandler {
    private TaskManager taskManager;
    private String response;
    private int statusCode;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.response = "";
        this.statusCode = HttpURLConnection.HTTP_OK;
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
                    handlePostSubtaskMethod(httpExchange);
                    break;
                case "DELETE":
                    handleDeleteTaskMethod(path);
                    break;
                default: {
                    statusCode = HttpURLConnection.HTTP_BAD_METHOD;
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
                statusCode = HttpURLConnection.HTTP_BAD_METHOD;
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
                statusCode = HttpURLConnection.HTTP_BAD_METHOD;
                response = "ID " + pathId + " is incorrect";
            } else {
                Subtask subtask = taskManager.getSubtaskById(id);
                if (subtask != null) {
                    response = gson.toJson(subtask);
                } else {
                    statusCode = HttpURLConnection.HTTP_NOT_FOUND;
                    response = "Subtask with ID " + id + " is not found.";
                }
            }
        } else {
            statusCode = HttpURLConnection.HTTP_BAD_METHOD;
            response = "There is no such endpoint for GET method.";
        }
    }

    public void handlePostSubtaskMethod(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();

        if (path.split("/").length == 2) {
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            if (body.isEmpty()) {
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
                response = "Body request is empty.";
            } else {
                try {
                    JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                    Subtask subtask = gson.fromJson(body, Subtask.class);

                    if (jsonObject.has("id") && subtask.getId() != -1) {
                        int subtaskId = subtask.getId();
                        taskManager.updateSubtask(subtask);
                        response = "Subtask with ID " + subtaskId + " was updated.";
                    } else {
                        taskManager.createSubtask(subtask);
                        response = "Subtask was created.";
                    }
                    statusCode = HttpURLConnection.HTTP_CREATED;

                } catch (TaskOverlappingException exc) {
                    statusCode = HttpURLConnection.HTTP_NOT_ACCEPTABLE;
                    response = "Can't create or update subtask. It overlaps with another task.";
                }
            }
        } else {
            statusCode = HttpURLConnection.HTTP_BAD_METHOD;
            response = "There is no such endpoint for POST method.";
        }
    }
}
