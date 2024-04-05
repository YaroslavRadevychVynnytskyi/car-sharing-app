package application.carsharingapp.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import application.carsharingapp.dto.user.UserLoginRequestDto;
import application.carsharingapp.dto.user.UserLoginResponseDto;
import application.carsharingapp.dto.user.UserRegistrationRequestDto;
import application.carsharingapp.dto.user.UserResponseDto;
import application.carsharingapp.model.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {
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
    @Sql(scripts = "classpath:database/roles/add-mock-roles-to-roles-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/users_roles/remove-mock-data-from-users_roles-table.sql",
            "classpath:database/roles/remove-mock-roles-from-roles-table.sql",
            "classpath:database/users/remove-mock-user-from-users-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Register")
    void register_AllOk_Success() throws Exception {
        //Given
        UserRegistrationRequestDto requestDto = getUserRegistrationDtoMock();
        UserResponseDto expected = getUserResponseDtoMock();
        String requestJson = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(
                post("/auth/register")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
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
    @DisplayName("Login")
    void login_AllOk_Success() throws Exception {
        //Given
        UserLoginRequestDto requestDto = new UserLoginRequestDto(
                "dave@email.com",
                "fooFighters"
        );

        String requestJson = objectMapper.writeValueAsString(requestDto);

        //When
        MvcResult result = mockMvc.perform(
                post("/auth/login")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        UserLoginResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserLoginResponseDto.class);

        System.out.println(actual.token());

        Assertions.assertNotNull(actual);
    }

    private UserRegistrationRequestDto getUserRegistrationDtoMock() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("james@email.com");
        requestDto.setFirstName("James");
        requestDto.setLastName("Hatfield");
        requestDto.setPassword("jamesPass1234");
        requestDto.setRepeatPassword("jamesPass1234");
        return requestDto;
    }

    private UserResponseDto getUserResponseDtoMock() {
        return new UserResponseDto(
                "james@email.com",
                "James",
                "Hatfield",
                Role.RoleName.CUSTOMER
        );
    }
}
