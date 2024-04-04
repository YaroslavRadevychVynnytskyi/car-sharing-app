package application.carsharingapp.repository.payment;

import application.carsharingapp.model.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRentalId(Long rentalId);

    @Query("SELECT payment FROM Payment payment "
            + "LEFT JOIN FETCH payment.rental rental "
            + "WHERE rental.id IN :rentalIds")
    List<Payment> findAllByRentalsId(@Param("rentalIds") List<Long> rentalsIds);
}
