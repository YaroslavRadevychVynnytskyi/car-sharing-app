package application.carsharingapp.exception;

public class RentalReturnException extends RuntimeException {
    public RentalReturnException(String message) {
        super(message);
    }
}
