package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int currentId;
    private HashMap<Integer, Task> taskHashMap;
    private HashMap<Integer, Epic> epicHaspMap;
    private HashMap<Integer, Subtask> subtaskHashMap;

    private HashMap<Epic, ArrayList<Subtask>> subtasksInEpicHashMap;

    public TaskManager() {
        this.currentId = 0;
        this.taskHashMap = new HashMap<>();
        this.epicHaspMap = new HashMap<>();
        this.subtaskHashMap = new HashMap<>();
        this.subtasksInEpicHashMap = new HashMap<>();
    }

    public void createTask(Task task) {
        taskHashMap.put(currentId, task);
        task.setId(currentId);
        currentId++;
    }

    public void createEpic(Epic epic) {
        epicHaspMap.put(currentId, epic);
        epic.setId(currentId);
        subtasksInEpicHashMap.put(epic, new ArrayList<>());
        currentId++;
    }

    public void createSubtask(Subtask subtask) {
        subtaskHashMap.put(currentId, subtask);
        subtask.setId(currentId);

        int subtaskEpicId = subtask.getEpicId();
        Epic epicForSubtask = epicHaspMap.get(subtaskEpicId);

        ArrayList<Subtask> subtasksInEpic = subtasksInEpicHashMap.get(epicForSubtask);
        subtasksInEpic.add(subtask); // обновила список подтасков эпика в Таск Менеджере

        epicForSubtask.setSubtasks(subtasksInEpic); // обновила список подтасков в объекте эпика

        updateEpicStatus(epicForSubtask);
        currentId++;
    }

    public void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasks = subtasksInEpicHashMap.get(epic);

        if (subtasks.size() == 0) {
            epic.setStatus(Status.NEW);
            return;
        }
        int newSubtask = 0;
        int doneSubtasks = 0;
        for (Subtask subtask : subtasks) {
            Status subtaskStatus = subtask.getStatus();
            if (subtaskStatus == Status.NEW) {
                newSubtask++;
            } else if (subtaskStatus == Status.DONE) {
                doneSubtasks++;
            }
        }
        if (newSubtask == subtasks.size()) {
            epic.setStatus(Status.NEW);
            return;
        }
        if (doneSubtasks == subtasks.size()) {
            epic.setStatus(Status.DONE);
            return;
        }
        epic.setStatus(Status.IN_PROGRESS);
        return;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasksList = new ArrayList<>(taskHashMap.values());
        return tasksList;
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> subtasksList = new ArrayList<>(subtaskHashMap.values());
        return subtasksList;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epicsList = new ArrayList<>(epicHaspMap.values());
        return epicsList;
    }

    public void removeAllTasks() {
        taskHashMap.clear();
    }

    public void removeAllSubtasks() {
        ArrayList<Integer> subtasksIds = new ArrayList<>(subtaskHashMap.keySet());
        for (int id : subtasksIds) {
            removeSubtaskById(id);
        }
    }

    public void removeAllEpics() {
        ArrayList<Integer> epicsIds = new ArrayList<>(epicHaspMap.keySet());
        for (int id : epicsIds) {
            removeEpicById(id);
        }
    }

    public void removeById(int id) {
        if (taskHashMap.containsKey(id)) {
            taskHashMap.remove(id);
        } else if (subtaskHashMap.containsKey(id)) {
            removeSubtaskById(id);
        } else if (epicHaspMap.containsKey(id)) {
            removeEpicById(id);
        } else {
            System.out.println("There is no task with such id :(");
        }
    }

    //    public void removeByIdTask(int id) {
//        taskHashMap.remove(id);
//    }
    public void removeSubtaskById(int id) {
        Subtask subtask = subtaskHashMap.get(id);
        int epicId = subtask.getEpicId();
        Epic epic = epicHaspMap.get(epicId);

        subtasksInEpicHashMap.get(epic).remove(subtask); // удалили подзадачу из эпика
        subtaskHashMap.remove(id); // удалили подзадачу из общего списка подзадач
        updateEpicStatus(epic);
    }

    public void removeEpicById(int id) {
        Epic epic = epicHaspMap.get(id);
        ArrayList<Subtask> subtasks = getSubtasksFromEpic(epic);
        for (Subtask subtask : subtasks) {
            removeSubtaskById(subtask.getId()); // удаляем подзадачу + удаляем её из эпика
        }
        epicHaspMap.remove(id); // удалили эпик из общего списка эпиков
    }

    public ArrayList<Subtask> getSubtasksFromEpic(Epic epic) {
        return subtasksInEpicHashMap.get(epic);
    }

    public Task getTaskById(int id) {
        return taskHashMap.get(id);
    }
    public Subtask getSubtaskById(int id) {
        return subtaskHashMap.get(id);
    }
    public Epic getEpicById(int id) {
        return epicHaspMap.get(id);
    }
}
