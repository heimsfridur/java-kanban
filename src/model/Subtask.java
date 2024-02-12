package model;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId, Status status, int id) {
        super(name, description, status);
        this.epicId = epicId;
        super.setId(id);
    }

    public Subtask(String name, String description, int epicId, Status status) {
        super(name, description, status);
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
}
