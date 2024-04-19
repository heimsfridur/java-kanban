package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;
    private TaskManager taskManager;

    private Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Duration.class, new DurationAdapter())
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
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

    @Test
    public void shouldGetAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "StatusCode is incorrect.");

        ArrayList<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());

        assertEquals(2, tasks.size(), "Number of tasks is incorrect.");
    }

    @Test
    public void shouldGetAllSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "StatusCode is incorrect.");

        ArrayList<Task> subtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());

        assertEquals(2, subtasks.size(), "Number of tasks is incorrect.");
    }

    @Test
    public void shouldGetAllEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "StatusCode is incorrect.");

        ArrayList<Task> epics = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());

        assertEquals(1, epics.size(), "Number of epics is incorrect.");
    }

    @Test
    public void shouldGetTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "StatusCode is incorrect.");
        Task task = gson.fromJson(response.body(), Task.class);
        assertEquals(task2, task, "Get wrong task.");

        URI uriWithWrongId = URI.create("http://localhost:8080/tasks/100");
        HttpRequest requestWithWrongId = HttpRequest.newBuilder()
                .GET()
                .uri(uriWithWrongId)
                .build();
        HttpResponse<String> response2 = client.send(requestWithWrongId, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response2.statusCode(), "StatusCode is incorrect.");
    }

    @Test
    public void shouldGetSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/4");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "StatusCode is incorrect.");
        Task subtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtask2Epic1, subtask, "Get wrong subtask.");

        URI uriWithWrongId = URI.create("http://localhost:8080/subtasks/100");
        HttpRequest requestWithWrongId = HttpRequest.newBuilder()
                .GET()
                .uri(uriWithWrongId)
                .build();
        HttpResponse<String> response2 = client.send(requestWithWrongId, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response2.statusCode(), "StatusCode is incorrect.");
    }

    @Test
    public void shouldGetEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "StatusCode is incorrect.");
        Epic epic = gson.fromJson(response.body(), Epic.class);
        assertEquals(epic1, epic, "Get wrong epic.");

        URI uriWithWrongId = URI.create("http://localhost:8080/epics/100");
        HttpRequest requestWithWrongId = HttpRequest.newBuilder()
                .GET()
                .uri(uriWithWrongId)
                .build();
        HttpResponse<String> response2 = client.send(requestWithWrongId, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response2.statusCode(), "StatusCode is incorrect.");
    }

    @Test
    public void shouldGetSubtasksFromEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/2/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ArrayList<Subtask> subtasksActual = gson.fromJson(response.body(),
                new TypeToken<ArrayList<Subtask>>(){}.getType());
        ArrayList<Subtask> subtasksExpected = taskManager.getSubtasksFromEpic(epic1);

        assertEquals(200, response.statusCode(), "StatusCode is incorrect.");
        assertEquals(subtasksExpected, subtasksActual, "Subtasks from epic are incorrect.");
    }

    @Test
    public void shouldCreateNewTask() throws IOException, InterruptedException {
        //5
        Task task3 = new Task("task3", "task3_descr", Status.IN_PROGRESS,
                LocalDateTime.of(2028, 4, 16, 12, 30), Duration.ofMinutes(30));

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task3)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "StatusCode is incorrect.");
    }
    @Test
    public void shouldNotCreateOverlappingTasks() throws IOException, InterruptedException {
        //5
        Task task3 = new Task("task3", "task3_descr", Status.IN_PROGRESS,
                LocalDateTime.of(2024, 4, 16, 12, 33), Duration.ofMinutes(30));

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task3)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "StatusCode is incorrect.");
    }

    @Test
    public void shouldUpdateTasks() throws IOException, InterruptedException {
        //5
        Task task1New = new Task("task1_new", "task1_new", Status.IN_PROGRESS, 0,
                LocalDateTime.of(2028, 4, 16, 12, 33), Duration.ofMinutes(30));

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1New)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "StatusCode is incorrect.");
    }

    @Test
    public void shouldCreateNewSubtask() throws IOException, InterruptedException {
        //5
        Subtask subtask3Epic1 = new Subtask("subtask3Epic1", "subtask3Epic1_descr", epic1.getId(),
                Status.NEW,
                LocalDateTime.of(2028, 4, 16, 19, 30), Duration.ofMinutes(120));

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask3Epic1)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "StatusCode is incorrect.");
    }

    @Test
    public void shouldUpdateSubtasks() throws IOException, InterruptedException {
        //5
        Subtask subtask2Epic1New = new Subtask("subtask2Epic1", "subtask2Epic1_descr",
                epic1.getId(), Status.DONE, subtask2Epic1.getId(),
                LocalDateTime.of(2024, 4, 16, 19, 30), Duration.ofMinutes(120));

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask2Epic1New)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "StatusCode is incorrect.");
    }

    @Test
    public void shouldCreateNewEpic() throws IOException, InterruptedException {
        //5
        Epic epicNew = new Epic("neeeew epic", "new rpic descr");


        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epicNew)))
                .uri(uri)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "StatusCode is incorrect.");
    }

    @Test
    public void shouldUpdateEpic() throws IOException, InterruptedException {
            Epic epic1New = new Epic("epic1", "epic1_descr", epic1.getId());


            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1New)))
                    .uri(uri)
                    .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "StatusCode is incorrect.");
    }

    @Test
    public void shouldGetHistory() throws IOException, InterruptedException {
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "StatusCode is incorrect.");

        ArrayList<Task> history = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>(){}.getType());

        assertEquals(2, history.size(), "Number of tasks in history is incorrect.");
        assertEquals(task1, history.getFirst(), "First task in history is incorrect.");


    }

    @Test
    public void shouldGetPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "StatusCode is incorrect.");

        List<Task> prioritizedTasks = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        assertEquals(4, prioritizedTasks.size(), "Number of tasks in prioritized tasks is incorrect.");
        assertEquals(subtask1Epic1.getId(), prioritizedTasks.getFirst().getId(),
                "First task in prioritized tasks is incorrect.");
    }


}
