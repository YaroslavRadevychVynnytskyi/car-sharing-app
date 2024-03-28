package application.carsharingapp.mapper;

import application.carsharingapp.config.MapperConfig;
import application.carsharingapp.dto.cars.CarListDto;
import application.carsharingapp.dto.cars.GenericCarDto;
import application.carsharingapp.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    Car toModel(GenericCarDto carDto);

    GenericCarDto toGenericCarDto(Car car);

    CarListDto toCarListDto(Car car);

    @Mapping(target = "id", ignore = true)
    void updateCarFromDto(GenericCarDto carDto, @MappingTarget Car car);
}
