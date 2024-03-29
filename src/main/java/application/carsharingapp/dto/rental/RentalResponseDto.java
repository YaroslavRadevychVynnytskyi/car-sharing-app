package application.carsharingapp.dto.rental;

import java.time.LocalDate;
import lombok.Data;

@Data
public class RentalResponseDto {
    private Long rentalId;
    private Long userId;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private Long carId;
}
