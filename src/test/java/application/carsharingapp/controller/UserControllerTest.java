package application.carsharingapp.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import application.carsharingapp.dto.user.UserRegistrationRequestDto;
import application.carsharingapp.dto.user.UserResponseDto;
import application.carsharingapp.model.Role;
import application.carsharingapp.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.Set;
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
public class UserControllerTest {
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
            "classpath:database/users_roles/add-mock-data-to-users_roles-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/users_roles/remove-mock-data-from-users_roles-table.sql",
            "classpath:database/roles/remove-mock-roles-from-roles-table.sql",
            "classpath:database/users/remove-mock-user-from-users-table.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get profile info")
    void getProfileInfo_AllOk_Success() throws Exception {
        //Given
        UserResponseDto expected = getUserResponseDtoMock();

        Authentication authentication = authenticateMockCustomer(getCustomerMock());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //When
        MvcResult result = mockMvc.perform(
                get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        UserResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Sql(scripts = {
            "classpath:database/roles/add-mock-roles-to-roles-table.sql",
            "classpath:database/users/add-mock-user-to-users-table.sql",
            "classpath:database/users_roles/add-mock-data-to-users_roles-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/users_roles/remove-mock-data-from-users_roles-table.sql",
            "classpath:database/roles/remove-mock-roles-from-roles-table.sql",
            "classpath:database/users/remove-mock-user-from-users-table.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Update profile info")
    void updateProfileInfo_AllOk_Success() throws Exception {
        //Given
        Authentication authentication = authenticateMockCustomer(getCustomerMock());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserRegistrationRequestDto requestDto = getUserRegistrationRequestDtoMock();

        UserResponseDto expected = new UserResponseDto(
                "mock@email.com",
                "Peter",
                "Stones",
                Role.RoleName.CUSTOMER
        );

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(
                patch("/users/me")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        UserResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserResponseDto.class);

        Assertions.assertEquals(expected, actual);
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

    private UserResponseDto getUserResponseDtoMock() {
        return new UserResponseDto(
                "mock@email.com",
                "Peter",
                "Jones",
                Role.RoleName.CUSTOMER
        );
    }

    private UserRegistrationRequestDto getUserRegistrationRequestDtoMock() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("mock@email.com");
        requestDto.setFirstName("Peter");
        requestDto.setLastName("Stones");
        requestDto.setPassword("peterPass1234");
        requestDto.setRepeatPassword("peterPass1234");
        return requestDto;
    }
}
