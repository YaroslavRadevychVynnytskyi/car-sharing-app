package application.carsharingapp.dto.car;

import jakarta.validation.constraints.NotNull;

public record CarListDto(
        Long id,
        @NotNull
        String model,
        @NotNull
        String brand
) {
}
