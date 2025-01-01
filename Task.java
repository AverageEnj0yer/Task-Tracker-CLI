import java.time.LocalDateTime;

public class Task {
    static int counter = 0;
    int id;
    String description;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public Task(String description, int id) {
        this.description = description;
        this.id = id;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "todo";
    }
}