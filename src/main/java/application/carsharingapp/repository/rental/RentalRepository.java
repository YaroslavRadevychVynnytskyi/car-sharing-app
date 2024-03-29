package application.carsharingapp.repository.rental;

import application.carsharingapp.model.Rental;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RentalRepository extends JpaRepository<Rental, Long>,
        JpaSpecificationExecutor<Rental> {
    Optional<Rental> findByUserIdAndCarId(Long userId, Long carId);
}
