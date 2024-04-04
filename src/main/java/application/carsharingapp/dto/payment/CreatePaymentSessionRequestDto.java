package application.carsharingapp.dto.payment;

import application.carsharingapp.model.Payment;

public record CreatePaymentSessionRequestDto(
        Long rentalId,
        Payment.Type paymentType
) {
}
