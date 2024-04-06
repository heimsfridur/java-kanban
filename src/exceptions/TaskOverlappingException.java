package exceptions;

public class TaskOverlappingException extends RuntimeException {
    public TaskOverlappingException(String message) {
        super(message);
    }
}
