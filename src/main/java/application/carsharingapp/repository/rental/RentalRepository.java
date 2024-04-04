package application.carsharingapp.repository.rental;

import application.carsharingapp.model.Rental;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalRepository extends JpaRepository<Rental, Long>,
        JpaSpecificationExecutor<Rental> {
    List<Rental> findByUserIdAndCarId(Long userId, Long carId);

    List<Rental> findAllByUserId(Long userId);

    @Query("SELECT rental FROM Rental rental "
            + "LEFT JOIN FETCH rental.car car "
            + "LEFT JOIN FETCH rental.user user "
            + "WHERE rental.returnDate <= :now AND rental.actualReturnDate IS NULL")
    List<Rental> findOverdueRentals(@Param("now") LocalDate now);
}
