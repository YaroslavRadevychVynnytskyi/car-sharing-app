package application.carsharingapp.mapper;

import application.carsharingapp.config.MapperConfig;
import application.carsharingapp.dto.rental.CreateRentalRequestDto;
import application.carsharingapp.dto.rental.RentalResponseDto;
import application.carsharingapp.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(target = "car", ignore = true)
    Rental toModel(CreateRentalRequestDto requestDto);

    @Mappings({
            @Mapping(target = "userId", source = "user.id"),
            @Mapping(target = "carId", source = "car.id"),
            @Mapping(target = "rentalId", source = "rental.id")
    })
    RentalResponseDto toDto(Rental rental);
}
