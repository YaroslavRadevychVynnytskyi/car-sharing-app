package application.carsharingapp.controller;

import application.carsharingapp.dto.car.CarListDto;
import application.carsharingapp.dto.car.GenericCarDto;
import application.carsharingapp.service.car.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car management", description = "Endpoints for managing cars")
@RequiredArgsConstructor
@RequestMapping("/cars")
@RestController
public class CarController {
    private final CarService carService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping
    @Operation(summary = "Add a new car", description = "Adds a new car")
    public GenericCarDto addCar(@RequestBody GenericCarDto requestDto) {
        return carService.addCar(requestDto);
    }

    @GetMapping
    @Operation(summary = "Get a list of cars", description = "Provides a list of all cars")
    public List<CarListDto> getCars(Pageable pageable) {
        return carService.getAllCars(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get car's detailed information",
            description = "Gets car's detailed information by id")
    public GenericCarDto getCarsInfo(@PathVariable Long id) {
        return carService.getCarsDetailedInfo(id);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PatchMapping("/{id}")
    @Operation(summary = "Update car", description = "Updates a car")
    public GenericCarDto updateCar(@PathVariable Long id,
                                   @RequestBody GenericCarDto requestDto) {
        return carService.updateCar(id, requestDto);

    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete car", description = "Deletes a car")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteById(id);
    }
}
