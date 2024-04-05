package application.carsharingapp.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import application.carsharingapp.dto.rental.CreateRentalRequestDto;
import application.carsharingapp.dto.rental.RentalResponseDto;
import application.carsharingapp.model.Role;
import application.carsharingapp.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
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
public class RentalControllerTest {
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
            "classpath:database/cars/add-mock-cars-to-cars-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/rentals/remove-mock-rentals-from-rentals-table.sql",
            "classpath:database/cars/remove-mock-cars-from-cars-table.sql",
            "classpath:database/users_roles/remove-mock-data-from-users_roles-table.sql",
            "classpath:database/users/remove-mock-user-from-users-table.sql",
            "classpath:database/roles/remove-mock-roles-from-roles-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Add rental")
    void addRental_AllOk_Success() throws Exception {
        //Given
        CreateRentalRequestDto requestDto = new CreateRentalRequestDto(
                LocalDate.of(2024, 4, 5),
                LocalDate.of(2024, 4, 8),
                2L
        );

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        RentalResponseDto expected = getRentalResponseDtoMock();

        Authentication authentication = authenticateMockCustomer(getCustomerMock());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //When
        MvcResult result = mockMvc.perform(
                post("/rentals")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        RentalResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), RentalResponseDto.class);
        EqualsBuilder.reflectionEquals(expected, actual, "rentalId");
    }

    @Test
    @Sql(scripts = {
            "classpath:database/rentals/remove-mock-rentals-from-rentals-table.sql",
            "classpath:database/roles/add-mock-roles-to-roles-table.sql",
            "classpath:database/users/add-mock-user-to-users-table.sql",
            "classpath:database/users_roles/add-mock-data-to-users_roles-table.sql",
            "classpath:database/cars/add-mock-cars-to-cars-table.sql",
            "classpath:database/rentals/add-mock-rentals-to-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/rentals/remove-mock-rentals-from-rentals-table.sql",
            "classpath:database/cars/remove-mock-cars-from-cars-table.sql",
            "classpath:database/users_roles/remove-mock-data-from-users_roles-table.sql",
            "classpath:database/users/remove-mock-user-from-users-table.sql",
            "classpath:database/roles/remove-mock-roles-from-roles-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get rentals (customer mod)")
    void getRentals_WithMockCustomer_Success() throws Exception {
        //Given
        List<RentalResponseDto> expected = getRentalResponseListDtoMock();
        expected.remove(1);

        Authentication authentication = authenticateMockManager(getCustomerMock());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //When
        MvcResult result = mockMvc.perform(
                        get("/rentals/")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        RentalResponseDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), RentalResponseDto[].class);

        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @Sql(scripts = {
            "classpath:database/roles/add-mock-roles-to-roles-table.sql",
            "classpath:database/users/add-mock-user-to-users-table.sql",
            "classpath:database/users_roles/add-mock-data-to-users_roles-table.sql",
            "classpath:database/cars/add-mock-cars-to-cars-table.sql",
            "classpath:database/rentals/add-mock-rentals-to-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/rentals/remove-mock-rentals-from-rentals-table.sql",
            "classpath:database/cars/remove-mock-cars-from-cars-table.sql",
            "classpath:database/users_roles/remove-mock-data-from-users_roles-table.sql",
            "classpath:database/users/remove-mock-user-from-users-table.sql",
            "classpath:database/roles/remove-mock-roles-from-roles-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get rentals (manager mod")
    void getRentals_WithMockManager_Success() throws Exception {
        //Given
        RentalResponseDto responseDto = new RentalResponseDto();
        responseDto.setRentalId(2L);
        responseDto.setRentalDate(LocalDate.of(2024, 4, 7));
        responseDto.setReturnDate(LocalDate.of(2024, 4, 10));
        responseDto.setCarId(1L);
        responseDto.setUserId(2L);

        List<RentalResponseDto> expected = new ArrayList<>();
        expected.add(responseDto);

        Authentication authentication = authenticateMockManager(getManagerMock());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //When
        MvcResult result = mockMvc.perform(
                get("/rentals/2")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        RentalResponseDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), RentalResponseDto[].class);

        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
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

    private RentalResponseDto getRentalResponseDtoMock() {
        RentalResponseDto rentalResponseDto = new RentalResponseDto();
        rentalResponseDto.setRentalId(1L);
        rentalResponseDto.setRentalDate(LocalDate.of(2024, 4, 5));
        rentalResponseDto.setReturnDate(LocalDate.of(2024, 4, 8));
        rentalResponseDto.setCarId(2L);
        rentalResponseDto.setUserId(1L);
        return rentalResponseDto;
    }

    private Authentication authenticateMockCustomer(User customer) {
        return new UsernamePasswordAuthenticationToken(
                customer,
                customer.getPassword(),
                AuthorityUtils.createAuthorityList("ROLE_CUSTOMER")
        );
    }

    private Authentication authenticateMockManager(User manager) {
        return new UsernamePasswordAuthenticationToken(
                manager,
                manager.getPassword(),
                AuthorityUtils.createAuthorityList("ROLE_MANAGER")
        );
    }

    private List<RentalResponseDto> getRentalResponseListDtoMock() {

        RentalResponseDto responseDto2 = new RentalResponseDto();
        responseDto2.setRentalId(2L);
        responseDto2.setRentalDate(LocalDate.of(2024, 4, 7));
        responseDto2.setReturnDate(LocalDate.of(2024, 4, 10));
        responseDto2.setCarId(1L);
        responseDto2.setUserId(2L);

        RentalResponseDto responseDto3 = new RentalResponseDto();
        responseDto3.setRentalId(3L);
        responseDto3.setRentalDate(LocalDate.of(2024, 4, 9));
        responseDto3.setReturnDate(LocalDate.of(2024, 4, 12));
        responseDto3.setCarId(1L);
        responseDto3.setUserId(1L);

        RentalResponseDto responseDto1 = getRentalResponseDtoMock();

        List<RentalResponseDto> responseDtoList = new ArrayList<>();
        responseDtoList.add(responseDto1);
        responseDtoList.add(responseDto2);
        responseDtoList.add(responseDto3);

        return responseDtoList;
    }
}
