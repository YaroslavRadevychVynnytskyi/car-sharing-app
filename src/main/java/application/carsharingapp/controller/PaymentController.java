package application.carsharingapp.controller;

import application.carsharingapp.dto.payment.CancelPaymentResponseDto;
import application.carsharingapp.dto.payment.CreatePaymentSessionRequestDto;
import application.carsharingapp.dto.payment.PaymentResponseDto;
import application.carsharingapp.exception.BadRequestException;
import application.carsharingapp.model.User;
import application.carsharingapp.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("!test")
@Tag(name = "Payment management", description = "Endpoints for managing payments")
@RequiredArgsConstructor
@RequestMapping("/payments")
@RestController
public class PaymentController {
    private static final String ROLE_MANAGER = "ROLE_MANAGER";
    private static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";

    private final PaymentService paymentService;

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_CUSTOMER')")
    @GetMapping
    @Operation(summary = "Get payments",
            description = "Retrieves specific user's payments if invoked by MANAGER,"
            + "retrieves only customer's payments if invoked by CUSTOMER")
    public List<PaymentResponseDto> getPayments(Authentication authentication,
                                                @RequestParam(required = false) Long userId) {
        User user = (User) authentication.getPrincipal();
        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority()
                .equals(ROLE_MANAGER) && userId != null)) {
            return paymentService.getPayments(userId);
        }
        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority()
                .equals(ROLE_CUSTOMER) && userId == null)) {
            return paymentService.getPayments(user.getId());
        }
        throw new BadRequestException("Bad request");
    }

    @PostMapping("/create")
    @Operation(summary = "Create payment session",
            description = "Creates payment session powered by Stripe")
    public PaymentResponseDto createPaymentSession(
            @RequestBody CreatePaymentSessionRequestDto request
    ) {
        return paymentService.createPaymentSession(request);
    }

    @GetMapping("/success")
    @Operation(summary = "Check successful payment",
            description = "Endpoint for Stripe redirection")
    public PaymentResponseDto checkSuccessfulPayment(@RequestParam Long rentalId) {
        return paymentService.checkSuccessfulPayment(rentalId);
    }

    @GetMapping("/cancel")
    @Operation(summary = "Pause payment", description = "Endpoint for Stripe redirection")
    public CancelPaymentResponseDto cancelPayment(@RequestParam Long rentalId) {
        return paymentService.cancelPayment(rentalId);
    }
}
