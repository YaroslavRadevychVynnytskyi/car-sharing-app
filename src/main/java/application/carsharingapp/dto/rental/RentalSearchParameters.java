package application.carsharingapp.dto.rental;

public record RentalSearchParameters(
        Long[] user_id,
        Boolean is_active
) {
}
