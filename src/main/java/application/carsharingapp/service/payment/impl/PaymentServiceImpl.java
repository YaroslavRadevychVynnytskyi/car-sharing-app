package application.carsharingapp.service.payment.impl;

import static com.stripe.param.checkout.SessionCreateParams.LineItem;
import static com.stripe.param.checkout.SessionCreateParams.Mode;
import static com.stripe.param.checkout.SessionCreateParams.PaymentMethodOptions.AcssDebit.Currency;
import static com.stripe.param.checkout.SessionCreateParams.PaymentMethodType;

import application.carsharingapp.dto.payment.CancelPaymentResponseDto;
import application.carsharingapp.dto.payment.CreatePaymentSessionRequestDto;
import application.carsharingapp.dto.payment.PaymentResponseDto;
import application.carsharingapp.exception.EntityNotFoundException;
import application.carsharingapp.exception.PaymentException;
import application.carsharingapp.mapper.PaymentMapper;
import application.carsharingapp.model.Payment;
import application.carsharingapp.model.Rental;
import application.carsharingapp.repository.payment.PaymentRepository;
import application.carsharingapp.repository.rental.RentalRepository;
import application.carsharingapp.service.notification.NotificationService;
import application.carsharingapp.service.payment.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Profile("!test")
@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(1.3);
    private static final int UNIT_AMOUNT_MULTIPLIER = 100;
    private static final Long DEFAULT_CAR_IN_RENTAL_QUANTITY = 1L;
    private static final String PRODUCT_NAME = "Car rental";
    private static final String PRODUCT_DESCRIPTION = "Payment for rental of the car";
    private static final String COMPLETE_SESSION_STATUS = "complete";
    private static final String CANCEL_URL_MESSAGE = "Payment was cancelled, but it can "
            + "be made later (Session is available for 24 hours)";

    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final NotificationService notificationService;
    @Value("${stripe.success.url}")
    private String successUrl;
    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    @Override
    public List<PaymentResponseDto> getPayments(Long userId) {
        List<Rental> userRentals = rentalRepository.findAllByUserId(userId);
        List<Payment> userPayments = paymentRepository.findAllByRentalsId(userRentals
                .stream()
                .map(Rental::getId)
                .toList()
        );
        return userPayments.stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public PaymentResponseDto createPaymentSession(CreatePaymentSessionRequestDto request) {
        checkIfRentalIsPaid(request);
        long moneyToPay = calculateAmount(request);

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(PaymentMethodType.CARD)
                .setMode(Mode.PAYMENT)
                .setSuccessUrl(UriComponentsBuilder.fromUriString(getSuccessUrl(request
                        .rentalId())).build().toUri().toString())
                .setCancelUrl(UriComponentsBuilder.fromUriString(getCancelUrl(request
                        .rentalId())).build().toUri().toString())
                .addAllLineItem(Collections.singletonList(
                        LineItem.builder()
                                .setPriceData(
                                        LineItem.PriceData.builder()
                                                .setCurrency(Currency.USD.getValue())
                                                .setUnitAmount(moneyToPay
                                                        * UNIT_AMOUNT_MULTIPLIER)
                                                .setProductData(
                                                        LineItem.PriceData.ProductData.builder()
                                                                .setName(PRODUCT_NAME)
                                                                .setDescription(PRODUCT_DESCRIPTION)
                                                                .build()
                                                )
                                                .build()
                                )
                                .setQuantity(DEFAULT_CAR_IN_RENTAL_QUANTITY)
                                .build()
                )).build();
        Session session;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create Stripe session", e);
        }

        Payment payment = new Payment();
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(request.paymentType());
        payment.setRental(findById(request.rentalId()));
        payment.setAmount(BigDecimal.valueOf(moneyToPay));

        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public PaymentResponseDto checkSuccessfulPayment(Long rentalId) {
        Payment payment = paymentRepository.findByRentalId(rentalId).orElseThrow(() ->
                new EntityNotFoundException("Can't find payment with rental ID: " + rentalId));
        Session session;
        try {
            session = Session.retrieve(payment.getSessionId());
        } catch (StripeException e) {
            throw new RuntimeException("Failed to retrieve session from Stripe", e);
        }
        if (session.getStatus().equals(COMPLETE_SESSION_STATUS)) {
            payment.setStatus(Payment.Status.PAID);
            paymentRepository.save(payment);

            String notificationMessage = buildNotificationMessage(payment);
            notificationService.sendNotification(notificationMessage);

            return paymentMapper.toDto(payment);
        }
        throw new PaymentException("Payment is not completed");
    }

    @Override
    public CancelPaymentResponseDto cancelPayment(Long rentalId) {
        return new CancelPaymentResponseDto(CANCEL_URL_MESSAGE);
    }

    private void checkIfRentalIsPaid(CreatePaymentSessionRequestDto request) {
        Optional<Payment> payment = paymentRepository.findByRentalId(request.rentalId());
        if (payment.isPresent() && payment.get().getStatus().equals(Payment.Status.PAID)) {
            throw new PaymentException("Rental with ID: " + request.rentalId()
                    + " is already paid");
        }
    }

    private Long calculateAmount(CreatePaymentSessionRequestDto request) {
        Rental rental = findById(request.rentalId());
        BigDecimal dailyFee = rental.getCar().getDailyFee();
        BigDecimal moneyToPay;
        if (request.paymentType().equals(Payment.Type.FINE)) {
            moneyToPay = BigDecimal.valueOf(rental.getReturnDate().getDayOfYear()
                    - rental.getRentalDate().getDayOfYear())
                    .multiply(dailyFee);
            BigDecimal overdueAmount = BigDecimal.valueOf(
                    rental.getActualReturnDate().getDayOfYear()
                    - rental.getReturnDate().getDayOfYear())
                    .multiply(dailyFee)
                    .multiply(FINE_MULTIPLIER);
            return moneyToPay.add(overdueAmount).longValue();
        }
        moneyToPay = BigDecimal.valueOf(rental.getReturnDate().getDayOfYear()
                        - rental.getRentalDate().getDayOfYear())
                .multiply(dailyFee);
        return moneyToPay.longValue();
    }

    private String buildNotificationMessage(Payment payment) {
        return "Payment Successful!\n"
                + "\nAmount: $" + payment.getAmount()
                + "\nDate: " + LocalDateTime.now()
                + "\nPayment ID: " + payment.getId()
                + "\nCustomer ID: " + payment.getRental().getUser().getId()
                + "\nCustomer Name: " + payment.getRental().getUser().getFirstName() + " "
                + payment.getRental().getUser().getLastName();
    }

    private String getSuccessUrl(Long rentalId) {
        return successUrl + rentalId;
    }

    private String getCancelUrl(Long rentalId) {
        return cancelUrl + rentalId;
    }

    private Rental findById(Long rentalId) {
        return rentalRepository.findById(rentalId).orElseThrow(() ->
                new EntityNotFoundException("Can't find rental with ID: " + rentalId));
    }
}
