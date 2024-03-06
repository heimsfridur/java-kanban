package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subtasksIds = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description);
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

}
