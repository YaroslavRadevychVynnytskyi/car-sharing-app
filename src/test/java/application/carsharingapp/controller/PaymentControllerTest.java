package application.carsharingapp.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import application.carsharingapp.dto.payment.CreatePaymentSessionRequestDto;
import application.carsharingapp.dto.payment.PaymentResponseDto;
import application.carsharingapp.model.Payment;
import application.carsharingapp.model.Role;
import application.carsharingapp.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @Sql(scripts = {
            "classpath:database/roles/add-mock-roles-to-roles-table.sql",
            "classpath:database/users/add-mock-user-to-users-table.sql",
            "classpath:database/users_roles/add-mock-data-to-users_roles-table.sql",
            "classpath:database/cars/add-mock-cars-to-cars-table.sql",
            "classpath:database/rentals/add-mock-rentals-to-rentals-table.sql",
            "classpath:database/payments/add-mock-payments-to-payments-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/payments/remove-mock-payments-from-payments-table.sql",
            "classpath:database/rentals/remove-mock-rentals-from-rentals-table.sql",
            "classpath:database/cars/remove-mock-cars-from-cars-table.sql",
            "classpath:database/users_roles/remove-mock-data-from-users_roles-table.sql",
            "classpath:database/users/remove-mock-user-from-users-table.sql",
            "classpath:database/roles/remove-mock-roles-from-roles-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get payments (customer access)")
    void getPayments_WithCustomerAssess_Success() throws Exception {
        //Given
        List<PaymentResponseDto> expected = getPaymentResponseDtoListMock();
        expected.remove(1);

        Authentication authentication = authenticateMockCustomer(getCustomerMock());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //When
        MvcResult result = mockMvc.perform(
                get("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PaymentResponseDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), PaymentResponseDto[].class);

        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @Sql(scripts = {
            "classpath:database/roles/add-mock-roles-to-roles-table.sql",
            "classpath:database/users/add-mock-user-to-users-table.sql",
            "classpath:database/users_roles/add-mock-data-to-users_roles-table.sql",
            "classpath:database/cars/add-mock-cars-to-cars-table.sql",
            "classpath:database/rentals/add-mock-rentals-to-rentals-table.sql",
            "classpath:database/payments/add-mock-payments-to-payments-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/payments/remove-mock-payments-from-payments-table.sql",
            "classpath:database/rentals/remove-mock-rentals-from-rentals-table.sql",
            "classpath:database/cars/remove-mock-cars-from-cars-table.sql",
            "classpath:database/users_roles/remove-mock-data-from-users_roles-table.sql",
            "classpath:database/users/remove-mock-user-from-users-table.sql",
            "classpath:database/roles/remove-mock-roles-from-roles-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get payments (manager access)")
    void getPayments_WithManagerAccess_Success() throws Exception {
        //Given
        List<PaymentResponseDto> expected = getPaymentResponseDtoListMock();
        expected.remove(0);

        Authentication authentication = authenticateMockCustomer(getManagerMock());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //When
        MvcResult result = mockMvc.perform(
                        get("/payments?userId=2")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PaymentResponseDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), PaymentResponseDto[].class);

        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @Sql(scripts = {
            "classpath:database/roles/add-mock-roles-to-roles-table.sql",
            "classpath:database/users/add-mock-user-to-users-table.sql",
            "classpath:database/users_roles/add-mock-data-to-users_roles-table.sql",
            "classpath:database/cars/add-mock-cars-to-cars-table.sql",
            "classpath:database/rentals/add-mock-rentals-to-rentals-table.sql",
            "classpath:database/payments/add-mock-payments-to-payments-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/payments/remove-mock-payments-from-payments-table.sql",
            "classpath:database/rentals/remove-mock-rentals-from-rentals-table.sql",
            "classpath:database/cars/remove-mock-cars-from-cars-table.sql",
            "classpath:database/users_roles/remove-mock-data-from-users_roles-table.sql",
            "classpath:database/users/remove-mock-user-from-users-table.sql",
            "classpath:database/roles/remove-mock-roles-from-roles-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Create payment session")
    void createPaymentSession_AllOk_Success() throws Exception {
        //Given
        CreatePaymentSessionRequestDto requestDto = new CreatePaymentSessionRequestDto(
                3L,
                Payment.Type.PAYMENT
        );

        PaymentResponseDto expected = new PaymentResponseDto(
                Payment.Status.PAID,
                Payment.Type.PAYMENT,
                3L,
                null,
                null,
                BigDecimal.valueOf(72)
        );

        Authentication authentication = authenticateMockCustomer(getCustomerMock());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String requestJson = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(
                post("/payments/create")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        PaymentResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), PaymentResponseDto.class);

        EqualsBuilder.reflectionEquals(expected, actual, "sessionUrl", "sessionId");
    }

    private List<PaymentResponseDto> getPaymentResponseDtoListMock() {
        PaymentResponseDto paymentResponseDto1 = new PaymentResponseDto(
                Payment.Status.PENDING,
                Payment.Type.PAYMENT,
                3L,
                "https://checkout.stripe.com/c/pay/cs_test_a1Wm7t2tVAf31fOGo5zFLOysLCL9hqR02Y8IqL7pAnDZ7oT2fhCee3tS2m#fidkdWxOYHwnPyd1blpxYHZxWjA0VTQwMVBMRGlUNkEzbG9oZnd%2FTlFDRkJ0XTRcYWAwN3RHQ1BPVDRJPFBLd1NwR1Bcc19nRk1zXWsybFYwUGhqXH90TFZsdVY9Qn9PPWk8aHFUTVxLcDw8NTVIUENjT040MCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl",
                "cs_test_a1Wm7t2tVAf31fOGo5zFLOysLCL9hqR02Y8IqL7pAnDZ7oT2fhCee3tS2m",
                BigDecimal.valueOf(72L)
        );
        PaymentResponseDto paymentResponseDto2 = new PaymentResponseDto(
                Payment.Status.PAID,
                Payment.Type.PAYMENT,
                4L,
                "https://checkout.stripe.com/c/pay/cs_test_a1Kb3y0xQpuZP2Ro7mJsbiqujZsZ6nQgNxIPJ3uv65Bi5fgARDtf0SIPxF#fidkdWxOYHwnPyd1blpxYHZxWjA0VTQwMVBMRGlUNkEzbG9oZnd%2FTlFDRkJ0XTRcYWAwN3RHQ1BPVDRJPFBLd1NwR1Bcc19nRk1zXWsybFYwUGhqXH90TFZsdVY9Qn9PPWk8aHFUTVxLcDw8NTVIUENjT040MCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl",
                "cs_test_a1Kb3y0xQpuZP2Ro7mJsbiqujZsZ6nQgNxIPJ3uv65Bi5fgARDtf0SIPxF",
                BigDecimal.valueOf(108L)
        );
        List<PaymentResponseDto> paymentResponseDtoList = new ArrayList<>();
        paymentResponseDtoList.add(paymentResponseDto1);
        paymentResponseDtoList.add(paymentResponseDto2);

        return paymentResponseDtoList;
    }

    private Authentication authenticateMockCustomer(User customer) {
        return new UsernamePasswordAuthenticationToken(
                customer,
                customer.getPassword(),
                AuthorityUtils.createAuthorityList("ROLE_CUSTOMER")
        );
    }

    private User getCustomerMock() {
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.CUSTOMER);

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = new User();
        user.setId(1L);
        user.setEmail("mockUser@email.com");
        user.setFirstName("John");
        user.setLastName("Peters");
        user.setPassword("peterPass1234");
        user.setRoles(roles);
        user.setDeleted(false);

        return user;
    }

    private User getManagerMock() {
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.MANAGER);

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = new User();
        user.setId(3L);
        user.setEmail("manager@email.com");
        user.setFirstName("Mr.");
        user.setLastName("Manager");
        user.setPassword("managerPass1234");
        user.setRoles(roles);
        user.setDeleted(false);

        return user;
    }
}
