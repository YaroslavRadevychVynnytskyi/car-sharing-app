package application.carsharingapp.dto.rental;

import java.time.LocalDate;

public record SetActualReturnDateRequestDto(
        Long carId,
        LocalDate actualReturnDate
) {
}
