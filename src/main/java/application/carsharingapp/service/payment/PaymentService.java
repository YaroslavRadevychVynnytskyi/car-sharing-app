package application.carsharingapp.service.payment;

import application.carsharingapp.dto.payment.CancelPaymentResponseDto;
import application.carsharingapp.dto.payment.CreatePaymentSessionRequestDto;
import application.carsharingapp.dto.payment.PaymentResponseDto;
import java.util.List;

public interface PaymentService {
    List<PaymentResponseDto> getPayments(Long userId);

    PaymentResponseDto createPaymentSession(CreatePaymentSessionRequestDto request);

    PaymentResponseDto checkSuccessfulPayment(Long rentalId);

    CancelPaymentResponseDto cancelPayment(Long rentalId);
}
