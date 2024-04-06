package service;

import com.sun.source.tree.Tree;
import exceptions.TaskOverlappingException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int currentId;
    protected HashMap<Integer, Task> taskHashMap;
    protected HashMap<Integer, Epic> epicHashMap;
    protected HashMap<Integer, Subtask> subtaskHashMap;
    protected HistoryManager inMemoryHistoryManager;
    protected TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        this.currentId = 0;
        this.taskHashMap = new HashMap<>();
        this.epicHashMap = new HashMap<>();
        this.subtaskHashMap = new HashMap<>();
        this.inMemoryHistoryManager = Managers.getDefaultHistory();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    @Override
    public void createTask(Task task) {
        task.setId(currentId);
        taskHashMap.put(currentId, task);
        currentId++;

        addToPrioritizedTasks(task);
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
        addToPrioritizedTasks(subtask);

        int epicId = subtask.getEpicId();
        Epic epic = epicHashMap.get(epicId);

        epic.getSubtasksIds().add(subtask.getId()); // обновила список подтасков в объекте эпика

        updateEpicStatus(epic);
        updateEpicTime(epic);
        currentId++;
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
        for (Task task : taskHashMap.values()) {
            inMemoryHistoryManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        taskHashMap.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : subtaskHashMap.values()) {
            prioritizedTasks.remove(subtask);
            inMemoryHistoryManager.remove(subtask.getId());
        }
        for (Epic epic : epicHashMap.values()) {
            epic.getSubtasksIds().clear();
            updateEpicStatus(epic);
            updateEpicTime(epic);
        }
        subtaskHashMap.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epicHashMap.values()) {
            for (Integer subtaskId : epic.getSubtasksIds()) {
                prioritizedTasks.remove(subtaskHashMap.get(subtaskId));
                inMemoryHistoryManager.remove(subtaskId);
            }
        }
        epicHashMap.clear();
        subtaskHashMap.clear();
    }

    @Override
    public void removeTaskById(int id) {
        Task task = taskHashMap.remove(id);
        prioritizedTasks.remove(task);
        inMemoryHistoryManager.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtaskHashMap.remove(id);

        int epicId = subtask.getEpicId();
        Epic epic = epicHashMap.get(epicId);

        epic.getSubtasksIds().remove(Integer.valueOf(id)); // удалили id подзадач из эпика
        prioritizedTasks.remove(subtask);
        inMemoryHistoryManager.remove(id);
        updateEpicStatus(epic);
        updateEpicTime(epic);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epicHashMap.remove(id);

        for (Integer subtaskId : epic.getSubtasksIds()) {
            Subtask subtask = subtaskHashMap.remove(subtaskId);
            prioritizedTasks.remove(subtask);
            inMemoryHistoryManager.remove(subtaskId);
        }
        inMemoryHistoryManager.remove(id);
    }

    @Override
    public ArrayList<Subtask> getSubtasksFromEpic(Epic epic) {
        return epic.getSubtasksIds().stream()
                .map(id -> subtaskHashMap.get(id))
                .collect(Collectors.toCollection(ArrayList::new));
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
        addToPrioritizedTasks(task);
        taskHashMap.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        subtaskHashMap.put(newSubtask.getId(), newSubtask); // заменила старую подзадачу на новую в списке подзадач
        addToPrioritizedTasks(newSubtask);

        Epic epic = epicHashMap.get(newSubtask.getEpicId());
        updateEpicStatus(epic);
        updateEpicTime(epic);
    }

    @Override
    public void updateEpic(Epic newEpic) {
        int epicId = newEpic.getId();
        Epic oldEpic = epicHashMap.get(epicId);
        oldEpic.setName(newEpic.getName());
        oldEpic.setDescription(newEpic.getDescription());
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    protected void updateEpicStatus(Epic epic) {
        if (epic.getSubtasksIds().stream()
                .allMatch(id -> Status.DONE.equals(subtaskHashMap.get(id).getStatus()))) {
            epic.setStatus(Status.DONE);
        } else if (epic.getSubtasksIds().stream()
                .allMatch(id -> Status.NEW.equals(subtaskHashMap.get(id).getStatus()))) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    protected void updateEpicTime(Epic epic) {
        ArrayList<Integer> subtasksIds = epic.getSubtasksIds();

         if (!subtasksIds.isEmpty()) {
            LocalDateTime earliestSubtaskTime = subtasksIds.stream()
                    .map(id -> subtaskHashMap.get(id).getStartTime())
                    .min(LocalDateTime::compareTo)
                    .orElse(null);
            LocalDateTime latestSubtaskTime = subtasksIds.stream()
                    .map(id -> subtaskHashMap.get(id).getEndTime())
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            Duration duration = subtasksIds.stream()
                    .map(id -> subtaskHashMap.get(id).getDuration())
                    .reduce(Duration.ZERO, (subtotal, element) -> subtotal.plus(element));

            epic.setStartTime(earliestSubtaskTime);
            epic.setEndTime(latestSubtaskTime);
            epic.setDuration(duration);
        }
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }


    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            if (validateOverlapping(task)) {
                throw new TaskOverlappingException("Tasks are overlapping.");
            }

            //удаляем объект из TreeSet (на случай обновления тасков)
            Task oldTask = taskHashMap.get(task.getId());
            if (oldTask != null)  {
                prioritizedTasks.remove(oldTask);
            }

            prioritizedTasks.add(task);
        }
    }

    private boolean validateOverlapping(Task taskForCheck) {
        boolean isOverlapping = prioritizedTasks.stream()
                .anyMatch(task -> {
                    LocalDateTime taskForCheckStart = taskForCheck.getStartTime();
                    LocalDateTime taskForCheckEnd = taskForCheck.getEndTime();
                    LocalDateTime taskStart = task.getStartTime();
                    LocalDateTime taskEnd = task.getEndTime();

                    return taskForCheckStart.isBefore(taskEnd) && taskForCheckEnd.isAfter(taskEnd) ||
                            taskStart.isBefore(taskForCheckEnd) && taskEnd.isAfter(taskForCheckEnd);
                });

        return isOverlapping;
    }
}
