package application.carsharingapp.service.rental;

import application.carsharingapp.dto.rental.CreateRentalRequestDto;
import application.carsharingapp.dto.rental.RentalResponseDto;
import application.carsharingapp.dto.rental.RentalSearchParameters;
import application.carsharingapp.dto.rental.SetActualReturnDateRequestDto;
import java.util.List;

public interface RentalService {
    RentalResponseDto addRental(Long userId, CreateRentalRequestDto request);

    List<RentalResponseDto> getCustomerRentals(Long userId);

    List<RentalResponseDto> getSpecificIdRentals(Long rentalId);

    void setActualReturnDate(Long userId, SetActualReturnDateRequestDto request);

    List<RentalResponseDto> searchRentals(RentalSearchParameters searchParameters);
}
