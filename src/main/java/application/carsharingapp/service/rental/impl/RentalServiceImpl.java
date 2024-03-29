package application.carsharingapp.service.rental.impl;

import application.carsharingapp.dto.rental.CreateRentalRequestDto;
import application.carsharingapp.dto.rental.RentalResponseDto;
import application.carsharingapp.dto.rental.RentalSearchParameters;
import application.carsharingapp.dto.rental.SetActualReturnDateRequestDto;
import application.carsharingapp.exception.EntityNotFoundException;
import application.carsharingapp.exception.NoAvailableCarsException;
import application.carsharingapp.mapper.RentalMapper;
import application.carsharingapp.model.Car;
import application.carsharingapp.model.Rental;
import application.carsharingapp.model.User;
import application.carsharingapp.repository.car.CarRepository;
import application.carsharingapp.repository.rental.RentalRepository;
import application.carsharingapp.repository.rental.RentalSpecificationBuilder;
import application.carsharingapp.repository.user.UserRepository;
import application.carsharingapp.service.rental.RentalService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {
    private static final int INVENTORY_CHANGE_AMOUNT = 1;

    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final RentalSpecificationBuilder rentalSpecificationBuilder;

    @Transactional
    @Override
    public RentalResponseDto addRental(Long userId, CreateRentalRequestDto request) {
        Rental rental = rentalMapper.toModel(request);
        User user = getUserById(userId);
        rental.setUser(user);

        Car car = getCarById(request.carId());

        if (car.getInventory() < 1) {
            throw new NoAvailableCarsException("There are no available cars "
                    + "with car ID: " + car.getId());
        }

        car.setInventory(car.getInventory() - INVENTORY_CHANGE_AMOUNT);
        rental.setCar(car);

        Rental savedRental = rentalRepository.save(rental);
        RentalResponseDto response = rentalMapper.toDto(savedRental);
        response.setRentalId(savedRental.getId());
        return response;
    }

    @Override
    public RentalResponseDto getSpecificRental(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(() ->
                new EntityNotFoundException("Can't find rental with id: " + rentalId));
        return rentalMapper.toDto(rental);
    }

    @Override
    public void setActualReturnDate(Long userId, SetActualReturnDateRequestDto request) {
        Rental rental = rentalRepository.findByUserIdAndCarId(userId, request.carId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find rental "
                        + "with user id: " + userId + " and car id: " + request.carId()));
        rental.setActualReturnDate(request.actualReturnDate());

        Car car = rental.getCar();
        car.setInventory(car.getInventory() + INVENTORY_CHANGE_AMOUNT);

        rentalRepository.save(rental);
    }

    @Override
    public List<RentalResponseDto> searchRentals(RentalSearchParameters searchParameters) {
        Specification<Rental> rentalSpecification =
                rentalSpecificationBuilder.build(searchParameters);
        return rentalRepository.findAll(rentalSpecification).stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Can't find user with id: " + userId));
    }

    private Car getCarById(Long carId) {
        return carRepository.findById(carId).orElseThrow(() ->
                new EntityNotFoundException("Can't find car with id: " + carId));
    }
}
