package application.carsharingapp.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import application.carsharingapp.dto.car.CarListDto;
import application.carsharingapp.dto.car.GenericCarDto;
import application.carsharingapp.model.Car;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {
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
    @Sql(scripts = "classpath:database/cars/add-mock-cars-to-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/remove-mock-cars-from-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get all cars")
    void getCars_AllOk_Success() throws Exception {
        //Given
        List<CarListDto> expected = getMockListOfCars();

        //When
        MvcResult result = mockMvc.perform(
                get("/cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        CarListDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), CarListDto[].class);
        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = "classpath:database/cars/remove-mock-cars-from-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Add car")
    void addCar_AllOk_Success() throws Exception {
        //Given
        GenericCarDto expected = getMockGenericCarDto();
        String jsonRequest = objectMapper.writeValueAsString(expected);

        //When
        MvcResult result = mockMvc.perform(
                post("/cars")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        GenericCarDto actual = objectMapper.readValue(result.getResponse()
                        .getContentAsString(), GenericCarDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @Sql(scripts = "classpath:database/cars/add-mock-cars-to-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/remove-mock-cars-from-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Update car")
    void updateCar() throws Exception {
        //Given
        GenericCarDto expected = getMockGenericCarDto();

        String jsonRequest = objectMapper.writeValueAsString(expected);

        //When
        MvcResult result = mockMvc.perform(
                patch("/cars/2")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        GenericCarDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), GenericCarDto.class);

        Assertions.assertEquals(expected, actual);
    }

    private List<CarListDto> getMockListOfCars() {
        CarListDto carListDto1 = new CarListDto(
                1L,
                "Cybertruck",
                "Tesla"
        );
        CarListDto carListDto2 = new CarListDto(
                2L,
                "Lanos",
                "Daewoo"
        );
        List<CarListDto> carListDtos = new ArrayList<>();
        carListDtos.add(carListDto1);
        carListDtos.add(carListDto2);
        return carListDtos;
    }

    private GenericCarDto getMockGenericCarDto() {
        GenericCarDto genericCarDto = new GenericCarDto();
        genericCarDto.setModel("911");
        genericCarDto.setBrand("Porsche");
        genericCarDto.setType(Car.Type.HATCHBACK);
        genericCarDto.setInventory(1);
        genericCarDto.setDailyFee(BigDecimal.valueOf(60L));
        return genericCarDto;
    }
}
