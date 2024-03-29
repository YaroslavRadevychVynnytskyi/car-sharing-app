package application.carsharingapp.dto.rental;

import java.time.LocalDate;

public record CreateRentalRequestDto(
        LocalDate rentalDate,
        LocalDate returnDate,
        Long carId
) {
}
