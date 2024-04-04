package application.carsharingapp.dto.payment;

import application.carsharingapp.model.Payment;
import java.math.BigDecimal;

public record PaymentResponseDto(
        Payment.Status status,
        Payment.Type type,
        Long rentalId,
        String sessionUrl,
        String sessionId,
        BigDecimal amount
) {
}
