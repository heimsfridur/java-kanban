package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subtasksIds = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description);
        this.subtasksIds = new ArrayList<>();
        super.setId(id);
    }

    public Epic(String name, String description, int id, LocalDateTime startTime, Duration duration) {
        super(name, description);
        this.subtasksIds = new ArrayList<>();
        super.setId(id);
    }

    public void setId(int id) {
        super.setId(id);
    }

    public int getId() {
        return super.getId();
    }

    public void setStatus(Status status) {
        super.setStatus(status);
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(ArrayList<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
