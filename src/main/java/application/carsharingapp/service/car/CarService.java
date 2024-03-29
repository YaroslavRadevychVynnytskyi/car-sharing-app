package application.carsharingapp.service.car;

import application.carsharingapp.dto.car.CarListDto;
import application.carsharingapp.dto.car.GenericCarDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CarService {
    GenericCarDto addCar(GenericCarDto requestDto);

    List<CarListDto> getAllCars(Pageable pageable);

    GenericCarDto getCarsDetailedInfo(Long carId);

    void deleteById(Long carId);

    GenericCarDto updateCar(Long carId, GenericCarDto requestDto);
}
