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
    private HashMap<Integer, Epic> epicHashMap;
    private HashMap<Integer, Subtask> subtaskHashMap;
    private HashMap<Integer, ArrayList<Integer>> subsConnectionsToEpics;

    public TaskManager() {
        this.currentId = 0;
        this.taskHashMap = new HashMap<>();
        this.epicHashMap = new HashMap<>();
        this.subtaskHashMap = new HashMap<>();
        this.subsConnectionsToEpics =new HashMap<>();
    }

    public void createTask(Task task) {
        taskHashMap.put(currentId, task);
        task.setId(currentId);
        currentId++;
    }

    public void createEpic(Epic epic) {
        epicHashMap.put(currentId, epic);
        epic.setId(currentId);
        subsConnectionsToEpics.put(epic.getId(), new ArrayList<>());
        currentId++;
    }

    public void createSubtask(Subtask subtask) {
        subtaskHashMap.put(currentId, subtask);
        subtask.setId(currentId);

        int epicId = subtask.getEpicId();
        Epic epic = epicHashMap.get(epicId);

        ArrayList<Integer> subtasksInEpic = epic.getSubtasks();
        subtasksInEpic.add(subtask.getId());
        epic.setSubtasks(subtasksInEpic); // обновила список подтасков в объекте эпика

        subsConnectionsToEpics.put(epicId, subtasksInEpic); // обновила список id подтасков эпика в Таск Менеджере

        updateEpicStatus(epic);
        currentId++;
    }

    public void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtasksIds = subsConnectionsToEpics.get(epic.getId());

        if (subtasksIds.size() == 0) {
            epic.setStatus(Status.NEW);
            return;
        }
        int newSubtask = 0;
        int doneSubtasks = 0;
        for (int subtaskId : subtasksIds) {
            Status subtaskStatus = getSubtaskById(subtaskId).getStatus();
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

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskHashMap.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtaskHashMap.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicHashMap.values());
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
        ArrayList<Integer> epicsIds = new ArrayList<>(epicHashMap.keySet());
        for (int id : epicsIds) {
            removeEpicById(id);
        }
    }

    public void removeById(int id) {
        if (taskHashMap.containsKey(id)) {
            taskHashMap.remove(id);
        } else if (subtaskHashMap.containsKey(id)) {
            removeSubtaskById(id);
        } else if (epicHashMap.containsKey(id)) {
            removeEpicById(id);
        } else {
            System.out.println("There is no task with such id :(");
        }
    }


    public void removeSubtaskById(int id) {
        Subtask subtask = subtaskHashMap.get(id);
        int epicId = subtask.getEpicId();
        Epic epic = epicHashMap.get(epicId);

        subsConnectionsToEpics.get(epicId).remove(Integer.valueOf(id)); // удалили id подзадач из эпика

        subtaskHashMap.remove(id); // удалили подзадачу из общего списка подзадач
        updateEpicStatus(epic);
    }

    public void removeEpicById(int id) {
        Epic epic = epicHashMap.get(id);
        ArrayList<Subtask> subtasks = getSubtasksFromEpic(epic);

        for (Subtask subtask : subtasks) {
            removeSubtaskById(subtask.getId());
        } // удаляем все подзадачи, принадлежавшие эпику

        epic.setSubtasks(new ArrayList<>()); // удалили все подзадачи внутри объекта эпика, теперь там пустой список
        epicHashMap.remove(id); // удалили эпик из общего списка эпиков
        subsConnectionsToEpics.remove(Integer.valueOf(id)); // удалили эпик из мапы сабтаск-эпик
    }

    public ArrayList<Subtask> getSubtasksFromEpic(Epic epic) {
        ArrayList<Integer> subtasksIds = subsConnectionsToEpics.get(epic.getId());
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (int id : subtasksIds) {
            subtasks.add(getSubtaskById(id));
        }
        return subtasks;
    }

    public Task getTaskById(int id) {
        return taskHashMap.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtaskHashMap.get(id);
    }

    public Epic getEpicById(int id) {
        return epicHashMap.get(id);
    }


    public void updateTask(Task task) {
        taskHashMap.put(task.getId(), task);
    }

    public void updateSubtask(Subtask newSubtask) {
        subtaskHashMap.put(newSubtask.getId(), newSubtask); // заменила старую подзадачу на новую в списке подзадач

        Epic epic = epicHashMap.get(newSubtask.getEpicId());
        updateEpicStatus(epic);
    }

}
