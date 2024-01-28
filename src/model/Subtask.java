package model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId, Status status) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public void setId(int id) {
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
