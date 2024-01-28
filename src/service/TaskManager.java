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
}
