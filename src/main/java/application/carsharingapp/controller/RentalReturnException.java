package application.carsharingapp.controller;

public class RentalReturnException extends RuntimeException {
    public RentalReturnException(String message) {
        super(message);
    }
}
