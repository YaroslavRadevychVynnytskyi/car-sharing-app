package application.carsharingapp.service.impl;

import application.carsharingapp.dto.cars.CarListDto;
import application.carsharingapp.dto.cars.GenericCarDto;
import application.carsharingapp.exception.EntityNotFoundException;
import application.carsharingapp.mapper.CarMapper;
import application.carsharingapp.model.Car;
import application.carsharingapp.repository.CarRepository;
import application.carsharingapp.service.CarService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public GenericCarDto addCar(GenericCarDto requestDto) {
        Car model = carMapper.toModel(requestDto);
        return carMapper.toGenericCarDto(carRepository.save(model));
    }

    @Override
    public List<CarListDto> getAllCars(Pageable pageable) {
        Page<Car> cars = carRepository.findAll(pageable);
        return cars.stream()
                .map(carMapper::toCarListDto)
                .toList();
    }

    @Override
    public GenericCarDto getCarsDetailedInfo(Long carId) {
        Car car = getCarById(carId);
        return carMapper.toGenericCarDto(car);
    }

    @Override
    public void deleteById(Long id) {
        if (!carRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't find car with id: " + id);
        }
        carRepository.deleteById(id);
    }

    @Override
    public GenericCarDto updateCar(Long id, GenericCarDto requestDto) {
        Car car = getCarById(id);
        carMapper.updateCarFromDto(requestDto, car);
        carRepository.save(car);
        return carMapper.toGenericCarDto(car);
    }

    private Car getCarById(Long carId) {
        return carRepository.findById(carId).orElseThrow(() ->
                new EntityNotFoundException("Can't find a car with id: " + carId));
    }
}
