package service;

import exceptions.ManagerSaveException;
import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File saveFile;

    public FileBackedTaskManager(File saveFile) {
        this.saveFile = saveFile;
    }

    public static void main(String[] args) {
        File file = new File("src/dataForLoading.csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        // 0
        Task task1 = new Task("task1", "task1_descr", Status.NEW);
        fileBackedTaskManager.createTask(task1);
        //1
        Task task2 = new Task("task2", "task2_descr", Status.NEW);
        fileBackedTaskManager.createTask(task2);

        //2
        Epic epic1 = new Epic("epic1", "epic1_descr");
        fileBackedTaskManager.createEpic(epic1);
        int epic1Id = epic1.getId();
        //3
        Subtask subtask1Epic1 = new Subtask("subtask1Epic1", "subtask1Epic1_descr", epic1Id, Status.DONE);
        fileBackedTaskManager.createSubtask(subtask1Epic1);

        //4
        Subtask subtask2Epic1 = new Subtask("subtask2Epic1", "subtask2Epic1_descr", epic1Id, Status.NEW);
        fileBackedTaskManager.createSubtask(subtask2Epic1);


        fileBackedTaskManager.getTaskById(1);
        fileBackedTaskManager.getEpicById(2);
        fileBackedTaskManager.getSubtaskById(4);
        fileBackedTaskManager.getTaskById(1);

        FileBackedTaskManager newManager = loadFromFile(file);

    }

    private void save() {
        String firstString = "id,type,name,status,description,epic\n";
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
            throw new ManagerSaveException("Ошибка сохранения в файл.");
        }
    }

    private String toString(Task task) {
        String epicIdForSubtask = "";
        if (task instanceof Subtask) {
            epicIdForSubtask += ((Subtask) task).getEpicId();
        }
        String res = task.getId() + "," + task.getType() + "," + task.getName() + "," +
                task.getStatus() + "," + task.getDescription() + "," + epicIdForSubtask + "\n";

        return res;
    }

    private static Task fromString(String value) {
        String[] taskPartsArray = value.split(",");
        int id = Integer.parseInt(taskPartsArray[0]);
        TaskType type = TaskType.valueOf(taskPartsArray[1]);
        String name = taskPartsArray[2];
        Status status = Status.valueOf(taskPartsArray[3]);
        String description = taskPartsArray[4];

        switch (type) {
            case TASK:
                return new Task(name, description, status, id);
            case SUBTASK:
                int epicId = Integer.parseInt(taskPartsArray[5]);
                return new Subtask(name, description, epicId, status, id);
            case EPIC:
                Epic epic = new Epic(name, description, id);
                epic.setSubtasksIds(new ArrayList<>());
                return epic;
            default:
                return null;
        }
    }

    static String historyToString(HistoryManager manager) {
        List<String> idsList = new ArrayList<>();

        List<Task> history = manager.getHistory();
        for (Task task : history) {
            idsList.add(String.valueOf(task.getId()));
        }

        return String.join(",", idsList);
    }



    static List<Integer> historyFromString(String value) {
        List<Integer> idsInHistory = new ArrayList<>();
        String[] idsArray = value.split(",");

        for (String id : idsArray) {
            idsInHistory.add(Integer.parseInt(id));
        }
        return idsInHistory;
    }

    static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
          String[] linesArray  = null;

        try {
            String str = Files.readString(file.toPath());
            linesArray =  str.split("\n");
        } catch (IOException exc) {
            throw new ManagerSaveException("Ошибка чтения файла.");
        }

        int maxId = 0;

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
            } else if (task instanceof Task) {
                fileBackedTaskManager.taskHashMap.put(id, task);
            }
        }
        fileBackedTaskManager.currentId = maxId + 1;

        for (Subtask subtask : fileBackedTaskManager.getAllSubtasks()) {
            int epicId = subtask.getEpicId();
            Epic epic = fileBackedTaskManager.getEpicById(epicId);
            epic.getSubtasksIds().add(subtask.getId());
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