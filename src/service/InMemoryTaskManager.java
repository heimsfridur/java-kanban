package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private int currentId;
    private HashMap<Integer, Task> taskHashMap;
    private HashMap<Integer, Epic> epicHashMap;
    private HashMap<Integer, Subtask> subtaskHashMap;
    private InMemoryHistoryManager inMemoryHistoryManager;

    public InMemoryTaskManager() {
        this.currentId = 0;
        this.taskHashMap = new HashMap<>();
        this.epicHashMap = new HashMap<>();
        this.subtaskHashMap = new HashMap<>();
        this.inMemoryHistoryManager = Managers.getDefaultHistory();
    }

    @Override
    public void createTask(Task task) {
        task.setId(currentId);
        taskHashMap.put(currentId, task);
        currentId++;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(currentId);
        epicHashMap.put(currentId, epic);
        currentId++;
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(currentId);
        subtaskHashMap.put(currentId, subtask);

        int epicId = subtask.getEpicId();
        Epic epic = epicHashMap.get(epicId);

        epic.getSubtasksIds().add(subtask.getId()); // обновила список подтасков в объекте эпика

        updateEpicStatus(epic);
        currentId++;
    }

    public void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtasksIds = epic.getSubtasksIds();

        if (subtasksIds.size() == 0) {
            epic.setStatus(Status.NEW);
            return;
        }
        int newSubtask = 0;
        int doneSubtasks = 0;
        for (int subtaskId : subtasksIds) {
            Status subtaskStatus = subtaskHashMap.get(subtaskId).getStatus();
            if (subtaskStatus == Status.NEW) {
                newSubtask++;
            } else if (subtaskStatus == Status.DONE) {
                doneSubtasks++;
            }
        }
        if (newSubtask == subtasksIds.size()) {
            epic.setStatus(Status.NEW);
            return;
        }
        if (doneSubtasks == subtasksIds.size()) {
            epic.setStatus(Status.DONE);
            return;
        }
        epic.setStatus(Status.IN_PROGRESS);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskHashMap.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtaskHashMap.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicHashMap.values());
    }

    @Override
    public void removeAllTasks() {
        taskHashMap.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epicHashMap.values()) {
            epic.getSubtasksIds().clear();
            updateEpicStatus(epic);
        }
        subtaskHashMap.clear();
    }

    @Override
    public void removeAllEpics() {
        epicHashMap.clear();
        subtaskHashMap.clear();
    }

    @Override
    public void removeTaskById(int id) {
        taskHashMap.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtaskHashMap.remove(id);

        int epicId = subtask.getEpicId();
        Epic epic = epicHashMap.get(epicId);

        epic.getSubtasksIds().remove(Integer.valueOf(id)); // удалили id подзадач из эпика

        updateEpicStatus(epic);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epicHashMap.remove(id);

        for (Integer subtaskId : epic.getSubtasksIds()) {
            subtaskHashMap.remove(subtaskId);
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksFromEpic(Epic epic) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (int id : epic.getSubtasksIds()) {
            subtasks.add(subtaskHashMap.get(id));
        }
        return subtasks;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = taskHashMap.get(id);
        inMemoryHistoryManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtaskHashMap.get(id);
        inMemoryHistoryManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicHashMap.get(id);
        inMemoryHistoryManager.add(epic);
        return epic;
    }


    @Override
    public void updateTask(Task task) {
        taskHashMap.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        subtaskHashMap.put(newSubtask.getId(), newSubtask); // заменила старую подзадачу на новую в списке подзадач

        Epic epic = epicHashMap.get(newSubtask.getEpicId());
        updateEpicStatus(epic);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

}
