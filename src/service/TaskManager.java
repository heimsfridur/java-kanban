package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Epic> getAllEpics();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    ArrayList<Subtask> getSubtasksFromEpic(Epic epic);

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void updateTask(Task task);

    void updateSubtask(Subtask newSubtask);

    void updateEpic(Epic newEpic);

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();
}
