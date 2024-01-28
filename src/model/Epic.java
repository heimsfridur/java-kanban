package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subtasks = new ArrayList<>();
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

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }
}
