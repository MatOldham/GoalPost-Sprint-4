package utsa.cs3773goalpost.ui.goals;

public class Goal {
    private String title;
    private String description;
    private String deadline;
    private String milestone;
    private String status;
    private int importance;

    // Constructor for Goal with all fields
    public Goal(String title, String description, String deadline, String milestone, String status, int importance) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.milestone = milestone;
        this.status = status;
        this.importance = importance;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getMilestone() {
        return milestone;
    }

    public void setMilestone(String milestone) {
        this.milestone = milestone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    // Method to update importance
    public void updateImportance(int newImportance) {
        this.importance = newImportance;
    }
}
