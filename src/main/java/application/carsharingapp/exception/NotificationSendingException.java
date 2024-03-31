package application.carsharingapp.exception;

public class NotificationSendingException extends RuntimeException {
    public NotificationSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
