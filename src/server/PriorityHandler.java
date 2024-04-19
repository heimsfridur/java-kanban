package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Pattern;

import static server.HttpTaskServer.gson;

public class PriorityHandler implements HttpHandler {
    private TaskManager taskManager;

    public PriorityHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "";
        int statusCode = 200;

        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();

            if (method.equals("GET")) {
                if (Pattern.matches("^/prioritized$", path)) {
                    response = gson.toJson(taskManager.getPrioritizedTasks());
                } else {
                    statusCode = 405;
                    response = "There is no such endpoint for GET method.";
                }
            } else {
                statusCode = 405;
                response = "There is no such endpoint.";
            }

        } catch (Exception exc) {
            exc.printStackTrace();
        }

        try (OutputStream os = httpExchange.getResponseBody()) {
            httpExchange.sendResponseHeaders(statusCode, 0);
            os.write(response.getBytes());
        }
    }
}
