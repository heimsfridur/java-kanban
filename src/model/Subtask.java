package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId, Status status, int id,
                   LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
        super.setId(id);
    }

    public Subtask(String name, String description, int epicId, Status status,
                   LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public void setId(int id) {
        if (epicId == id) {
            throw new IllegalArgumentException("Subtask cannot be its own epic");
        }
        super.setId(id);
    }

    public int getId() {
        return super.getId();
    }

    public int getEpicId() {
        return epicId;
    }

    public Status getStatus() {
        return super.getStatus();
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }
}
