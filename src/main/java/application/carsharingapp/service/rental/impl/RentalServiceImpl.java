package application.carsharingapp.service.rental.impl;

import application.carsharingapp.dto.rental.CreateRentalRequestDto;
import application.carsharingapp.dto.rental.RentalResponseDto;
import application.carsharingapp.dto.rental.RentalSearchParameters;
import application.carsharingapp.dto.rental.SetActualReturnDateRequestDto;
import application.carsharingapp.exception.EntityNotFoundException;
import application.carsharingapp.exception.NoAvailableCarsException;
import application.carsharingapp.exception.RentalReturnException;
import application.carsharingapp.mapper.RentalMapper;
import application.carsharingapp.model.Car;
import application.carsharingapp.model.Rental;
import application.carsharingapp.model.User;
import application.carsharingapp.repository.car.CarRepository;
import application.carsharingapp.repository.rental.RentalRepository;
import application.carsharingapp.repository.rental.RentalSpecificationBuilder;
import application.carsharingapp.repository.user.UserRepository;
import application.carsharingapp.service.notification.NotificationService;
import application.carsharingapp.service.rental.RentalService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {
    private static final String NEW_RENTAL_BOOKED_MESSAGE = "New rental booked:";
    private static final String OVERDUE_RENTAL_MESSAGE = "Overdue rental:";
    private static final String NO_OVERDUE_MESSAGE = "No rentals overdue today!";
    private static final int INVENTORY_CHANGE_AMOUNT = 1;

    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final RentalSpecificationBuilder rentalSpecificationBuilder;
    private final NotificationService notificationService;

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

        String notificationMessage = buildNotificationMessage(rental);
        notificationService.sendNotification(NEW_RENTAL_BOOKED_MESSAGE
                + notificationMessage);

        return response;
    }

    @Override
    public List<RentalResponseDto> getCustomerRentals(Long userId) {
        List<Rental> rentals = rentalRepository.findAllByUserId(userId);
        return rentals.stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public List<RentalResponseDto> getSpecificIdRentals(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(() ->
                new EntityNotFoundException("Can't find rental with id: " + rentalId));
        return List.of(rentalMapper.toDto(rental));
    }

    @Override
    public void setActualReturnDate(Long userId, SetActualReturnDateRequestDto request) {
        Rental rental = rentalRepository.findByUserIdAndCarId(userId, request.carId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find rental "
                        + "with user id: " + userId + " and car id: " + request.carId()));

        if (rental.getActualReturnDate() != null) {
            throw new RentalReturnException("Rental cannot be returned twice");
        }

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

    private String buildNotificationMessage(Rental rental) {
        return "\n         id: " + rental.getId()
                + "\n"
                + "\n 1. Customer:"
                + "\n         id: " + rental.getUser().getId()
                + "\n         name: " + rental.getUser().getFirstName()
                + " " + rental.getUser().getLastName()
                + "\n         email: " + rental.getUser().getEmail()
                + "\n"
                + "\n 2. Car:"
                + "\n         id: " + rental.getCar().getId()
                + "\n         brand: " + rental.getCar().getBrand()
                + "\n         model: " + rental.getCar().getModel()
                + "\n         daily fee: " + rental.getCar().getDailyFee()
                + "\n         inventory left: " + rental.getCar().getInventory()
                + "\n"
                + "\n 3. Period:"
                + "\n         rental date: " + rental.getRentalDate()
                + "\n         return date: " + rental.getReturnDate() + "\n";
    }

    @Scheduled(cron = "0 0 9 * * *")
    protected void checkOverdueRentals() {
        List<Rental> overdueRentals = rentalRepository.findOverdueRentals(LocalDate.now());
        if (!overdueRentals.isEmpty()) {
            overdueRentals.forEach(rental -> notificationService
                    .sendNotification(OVERDUE_RENTAL_MESSAGE
                            + buildNotificationMessage(rental)));
        } else {
            notificationService.sendNotification(NO_OVERDUE_MESSAGE);
        }
    }
}
