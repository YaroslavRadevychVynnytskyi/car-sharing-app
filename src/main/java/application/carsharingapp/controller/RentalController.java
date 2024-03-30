package application.carsharingapp.controller;

import application.carsharingapp.dto.rental.CreateRentalRequestDto;
import application.carsharingapp.dto.rental.RentalResponseDto;
import application.carsharingapp.dto.rental.RentalSearchParameters;
import application.carsharingapp.dto.rental.SetActualReturnDateRequestDto;
import application.carsharingapp.exception.BadRequestException;
import application.carsharingapp.model.User;
import application.carsharingapp.service.rental.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rental management", description = "Endpoints for managing rentals")
@RequiredArgsConstructor
@RequestMapping("/rentals")
@RestController
public class RentalController {
    private final RentalService rentalService;

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping
    @Operation(summary = "Add a new rental", description = "Adds a new rental")
    public RentalResponseDto addRental(Authentication authentication,
                                       @RequestBody CreateRentalRequestDto request) {
        User user = (User) authentication.getPrincipal();
        return rentalService.addRental(user.getId(), request);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/search/")
    @Operation(summary = "Get active rentals",
            description = "Gets rentals by user ID and whether the rental is still active or not")
    public List<RentalResponseDto> getActiveRentals(RentalSearchParameters searchParameters) {
        return rentalService.searchRentals(searchParameters);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_CUSTOMER')")
    @GetMapping(value = {"/{id}", "/"})
    @Operation(summary = "Get rental", description = "Gets rentals")
    public List<RentalResponseDto> getRentals(Authentication authentication,
                                              @PathVariable(required = false) Long id) {
        User user = (User) authentication.getPrincipal();
        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority()
                .equals("ROLE_MANAGER")) && id != null) {
            return rentalService.getSpecificIdRentals(id);
        }
        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority()
                .equals("ROLE_CUSTOMER")) && id == null) {
            return rentalService.getCustomerRentals(user.getId());
        }
        throw new BadRequestException("Bad request");
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping("/return")
    @Operation(summary = "Set actual return date",
            description = "Sets actual return date (increases car inventory by 1)")
    public void setActualReturnDate(Authentication authentication,
                                    @RequestBody SetActualReturnDateRequestDto request) {
        User user = (User) authentication.getPrincipal();
        rentalService.setActualReturnDate(user.getId(), request);
    }
}
