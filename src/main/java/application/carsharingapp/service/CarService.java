package application.carsharingapp.service;

import application.carsharingapp.dto.cars.CarListDto;
import application.carsharingapp.dto.cars.GenericCarDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CarService {
    GenericCarDto addCar(GenericCarDto requestDto);

    List<CarListDto> getAllCars(Pageable pageable);

    GenericCarDto getCarsDetailedInfo(Long carId);

    void deleteById(Long carId);

    GenericCarDto updateCar(Long carId, GenericCarDto requestDto);
}
