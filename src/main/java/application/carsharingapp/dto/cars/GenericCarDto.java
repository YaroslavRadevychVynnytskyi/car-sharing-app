package application.carsharingapp.dto.cars;

import application.carsharingapp.model.Car;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class GenericCarDto {
    @NotNull
    private String model;
    @NotNull
    private String brand;
    @NotNull
    private Car.Type type;
    @NotNull
    private Integer inventory;
    @Min(0)
    private BigDecimal dailyFee;
}
