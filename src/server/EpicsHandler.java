package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.TaskOverlappingException;
import model.Epic;
import model.Subtask;
import service.TaskManager;
import static server.HttpTaskServer.gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class EpicsHandler implements HttpHandler {
    private TaskManager taskManager;
    private String response;
    private int statusCode;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.response = "";
        this.statusCode = HttpURLConnection.HTTP_OK;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    handleGetEpicMethod(path);
                    break;
                case "POST":
                    handlePostEpicMethod(httpExchange);
                    break;
                case "DELETE":
                    handleDeleteEpicMethod(path);
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

    private void handleDeleteEpicMethod(String path) {
        if (Pattern.matches("^/epics/\\d+$", path)) {
            String pathId = path.replaceFirst("/epics/", "");
            int id = parsePathId(pathId);
            if (id == -1) {
                statusCode = HttpURLConnection.HTTP_BAD_METHOD;
                response = "The ID " + pathId + " for deletion is incorrect.";
            } else {
                taskManager.removeEpicById(id);
                response = "Epic with ID " + id + " was deleted.";
            }
        }
    }

    private void handleGetEpicMethod(String path) {
        if (Pattern.matches("^/epics$", path)) {
            response = gson.toJson(taskManager.getAllEpics());

        } else if (Pattern.matches("^/epics/\\d+$", path)) {
            String pathId = path.replaceFirst("/epics/", "");
            int id = parsePathId(pathId);
            if (id == -1) {
                statusCode = HttpURLConnection.HTTP_BAD_METHOD;
                response = "ID " + pathId + " is incorrect";
            } else {
                Epic epic = taskManager.getEpicById(id);
                if (epic != null) {
                    response = gson.toJson(epic);
                } else {
                    statusCode = HttpURLConnection.HTTP_NOT_FOUND;
                    response = "Epic with ID " + id + " is not found.";
                }
            }

        } else if (Pattern.matches("^/epics/\\d+/subtasks$", path)) {
            String pathId = path.replaceFirst("/epics/", "")
                    .replaceFirst("/subtasks$", "");
            int id = parsePathId(pathId);
            if (id == -1) {
                statusCode = HttpURLConnection.HTTP_BAD_METHOD;
                response = "ID " + pathId + " is incorrect";
            } else {
                Epic epic = taskManager.getEpicById(id);
                if (epic != null) {
                    ArrayList<Subtask> subtasksFromEpic = taskManager.getSubtasksFromEpic(epic);
                    response = gson.toJson(subtasksFromEpic);
                } else {
                    statusCode = HttpURLConnection.HTTP_NOT_FOUND;
                    response = "Epic with ID " + id + " is not found.";
                }
            }
        } else {
            statusCode = HttpURLConnection.HTTP_BAD_METHOD;
            response = "There is no such endpoint for GET method.";
        }
    }

    public void handlePostEpicMethod(HttpExchange httpExchange) throws IOException {
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
                    Epic epic = gson.fromJson(body, Epic.class);

                    if (jsonObject.has("id") && epic.getId() != -1) {
                        int taskId = epic.getId();
                        taskManager.updateEpic(epic);
                        response = "Epic with ID " + taskId + " was updated.";
                    } else {
                        taskManager.createEpic(epic);
                        response = "Epic was created.";
                    }
                    statusCode = HttpURLConnection.HTTP_CREATED;

                } catch (TaskOverlappingException exc) {
                    statusCode = HttpURLConnection.HTTP_NOT_ACCEPTABLE;
                    response = "Can't update epic. It overlaps with another task.";
                }
            }
        } else {
            statusCode = HttpURLConnection.HTTP_BAD_METHOD;
            response = "There is no such endpoint for POST method.";
        }
    }

}