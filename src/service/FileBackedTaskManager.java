package service;

import exceptions.ManagerSaveException;
import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File saveFile;

    public FileBackedTaskManager(File saveFile) {
        this.saveFile = saveFile;
    }

    public static void main(String[] args) {
        File file = new File("src/data.csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        // 0
        Task task1 = new Task("task1", "task1_descr", Status.NEW,
                LocalDateTime.of(2024, 4, 16, 12, 30), Duration.ofMinutes(30));
        fileBackedTaskManager.createTask(task1);
        //1
        Task task2 = new Task("task2", "task2_descr", Status.NEW,
                LocalDateTime.of(2024, 4, 28, 2, 30), Duration.ofMinutes(20));
        fileBackedTaskManager.createTask(task2);

        //2
        Epic epic1 = new Epic("epic1", "epic1_descr");
        fileBackedTaskManager.createEpic(epic1);
        int epic1Id = epic1.getId();
        //3
        Subtask subtask1Epic1 = new Subtask("subtask1Epic1", "subtask1Epic1_descr", epic1Id, Status.DONE,
                LocalDateTime.of(2024, 4, 10, 15, 0), Duration.ofMinutes(5));
        fileBackedTaskManager.createSubtask(subtask1Epic1);

        //4
        Subtask subtask2Epic1 = new Subtask("subtask2Epic1", "subtask2Epic1_descr", epic1Id, Status.NEW,
                LocalDateTime.of(2024, 4, 16, 19, 30), Duration.ofMinutes(120));
        fileBackedTaskManager.createSubtask(subtask2Epic1);


        fileBackedTaskManager.getTaskById(1);
        fileBackedTaskManager.getEpicById(2);
        fileBackedTaskManager.getSubtaskById(4);
        fileBackedTaskManager.getTaskById(1);

        FileBackedTaskManager newManager = loadFromFile(file);

    }

    private void save() {
        String firstString = "id,type,name,status,description,startTime,duration(min),epic\n";
        try (Writer fw = new FileWriter(saveFile)) {
            fw.write(firstString);

            for (Task task : getAllTasks()) {
                fw.write(toString(task));
            }

            for (Subtask subtask : getAllSubtasks()) {
                fw.write(toString(subtask));
            }

            for (Epic epic : getAllEpics()) {
                fw.write(toString(epic));
            }

            fw.write("\n");
            fw.write(historyToString(super.inMemoryHistoryManager));
        } catch (IOException exc) {
            throw new ManagerSaveException("File saving error.");
        }
    }

    private String toString(Task task) {
        String epicIdForSubtask = "";
        if (task instanceof Subtask) {
            epicIdForSubtask += ((Subtask) task).getEpicId();
        }
        return task.getId() + "," + task.getType() + "," + task.getName() + "," +
                task.getStatus() + "," + task.getDescription() + ","  +
                task.getStartTime() + "," + task.getDuration() + "," + epicIdForSubtask + "\n";
    }

    private static Task fromString(String value) {
        String[] taskPartsArray = value.split(",");
        int id = Integer.parseInt(taskPartsArray[0]);
        TaskType type = TaskType.valueOf(taskPartsArray[1]);
        String name = taskPartsArray[2];
        Status status = Status.valueOf(taskPartsArray[3]);
        String description = taskPartsArray[4];
        LocalDateTime startTime = LocalDateTime.parse(taskPartsArray[5]);
        Duration duration = Duration.parse(taskPartsArray[6]);


        switch (type) {
            case SUBTASK:
                int epicId = Integer.parseInt(taskPartsArray[7]);
                return new Subtask(name, description, epicId, status, id, startTime, duration);
            case EPIC:
                Epic epic = new Epic(name, description, id, startTime, duration);
                return epic;
            default:
                return new Task(name, description, status, id, startTime, duration);
        }
    }

    private static String historyToString(HistoryManager manager) {
        List<String> idsList = new ArrayList<>();

        List<Task> history = manager.getHistory();
        for (Task task : history) {
            idsList.add(String.valueOf(task.getId()));
        }

        return String.join(",", idsList);
    }



    private static List<Integer> historyFromString(String value) {
        return Arrays.stream(value.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
          String[] linesArray;

        try {
            String str = Files.readString(file.toPath());
            linesArray =  str.split("\n");
        } catch (IOException exc) {
            throw new ManagerSaveException("File reading error.");
        }

        int maxId = 0;
        int allTasksCount = 0;

        for (int i = 1; i < linesArray.length; i++) {
            String line = linesArray[i];
            if (line.isBlank()) {
                break;
            }
            Task task = fromString(line);
            int id = task.getId();
            if (id > maxId) {
                maxId = id;
            }

            if (task instanceof Subtask) {
                fileBackedTaskManager.subtaskHashMap.put(id, (Subtask) task);
            } else if (task instanceof Epic) {
                fileBackedTaskManager.epicHashMap.put(id, (Epic) task);
            } else {
                fileBackedTaskManager.taskHashMap.put(id, task);
            }
            allTasksCount++;
        }
        fileBackedTaskManager.currentId = maxId + 1;

        for (Subtask subtask : fileBackedTaskManager.getAllSubtasks()) {
            int epicId = subtask.getEpicId();
            Epic epic = fileBackedTaskManager.epicHashMap.get(epicId);
            epic.getSubtasksIds().add(subtask.getId());
        }

        for (Epic epic : fileBackedTaskManager.getAllEpics()) {
            fileBackedTaskManager.updateEpicStatus(epic);
            fileBackedTaskManager.updateEpicTime(epic);
        }

        if (linesArray.length > allTasksCount + 1) {
            List<Integer> historyIds = historyFromString(linesArray[linesArray.length - 1]);
            for (Integer id : historyIds) {
                if (fileBackedTaskManager.taskHashMap.containsKey(id)) {
                    Task task = fileBackedTaskManager.taskHashMap.get(id);
                    fileBackedTaskManager.inMemoryHistoryManager.add(task);
                } else if (fileBackedTaskManager.subtaskHashMap.containsKey(id)) {
                    Subtask subtask = fileBackedTaskManager.subtaskHashMap.get(id);
                    fileBackedTaskManager.inMemoryHistoryManager.add(subtask);
                } else if (fileBackedTaskManager.epicHashMap.containsKey(id)) {
                    Epic epic = fileBackedTaskManager.epicHashMap.get(id);
                    fileBackedTaskManager.inMemoryHistoryManager.add(epic);
                }
            }
        }

        return fileBackedTaskManager;
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }


    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }


}