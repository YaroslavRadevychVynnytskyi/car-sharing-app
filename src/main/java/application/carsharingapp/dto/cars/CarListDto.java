package application.carsharingapp.dto.cars;

import jakarta.validation.constraints.NotNull;

public record CarListDto(
        Long id,
        @NotNull
        String model,
        @NotNull
        String brand
) {
}
