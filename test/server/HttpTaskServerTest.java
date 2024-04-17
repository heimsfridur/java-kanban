package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;
    private TaskManager taskManager;

    private Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .create();
    private Task task1;
    private Task task2;
    private Subtask subtask1Epic1;
    private Subtask subtask2Epic1;
    private Epic epic1;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(taskManager);

        //0
        task1 = new Task("task1", "task1_descr", Status.NEW,
                LocalDateTime.of(2024, 4, 16, 12, 30), Duration.ofMinutes(30));
        taskManager.createTask(task1);
        //1
        task2 = new Task("task2", "task2_descr", Status.NEW,
                LocalDateTime.of(2024, 4, 28, 2, 30), Duration.ofMinutes(20));
        taskManager.createTask(task2);
        //2
        epic1 = new Epic("epic1", "epic1_descr");
        taskManager.createEpic(epic1);
        int epic1Id = epic1.getId();
        //3
        subtask1Epic1 = new Subtask("subtask1Epic1", "subtask1Epic1_descr", epic1Id, Status.DONE,
                LocalDateTime.of(2024, 4, 10, 15, 0), Duration.ofMinutes(5));
        taskManager.createSubtask(subtask1Epic1);
        //4
        subtask2Epic1 = new Subtask("subtask2Epic1", "subtask2Epic1_descr", epic1Id, Status.NEW,
                LocalDateTime.of(2024, 4, 16, 19, 30), Duration.ofMinutes(120));
        taskManager.createSubtask(subtask2Epic1);

        httpTaskServer.start();
    }

    @AfterEach
    void tearDown() {
        httpTaskServer.stop();
    }

    @Test
    void shouldDeleteTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "StatusCode is incorrect.");

        ArrayList<Task> tasksAfterDeletion = taskManager.getAllTasks();
        assertEquals(1, tasksAfterDeletion.size(), "Task wasn't deleted.");
        assertEquals(task2, tasksAfterDeletion.getFirst(), "Deletion of task was incorrect.");
    }

    @Test
    void shouldDeleteSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "StatusCode is incorrect.");

        ArrayList<Subtask> subtasksAfterDeletion = taskManager.getAllSubtasks();
        assertEquals(1, subtasksAfterDeletion.size(), "Task wasn't deleted.");
        assertEquals(subtask2Epic1, subtasksAfterDeletion.getFirst(), "Deletion of subtask was incorrect.");
    }

    @Test
    void shouldDeleteEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "StatusCode is incorrect.");

        ArrayList<Epic> epicsAfterDeletion = taskManager.getAllEpics();
        assertEquals(0, epicsAfterDeletion.size(), "Epic wasn't deleted.");

        ArrayList<Subtask> subtasksAfterTheOnlyEpicDeletion = taskManager.getAllSubtasks();
        assertEquals(0, subtasksAfterTheOnlyEpicDeletion.size(), "Subtasks were not" +
                "deleted after epic deletion.");
    }
}
