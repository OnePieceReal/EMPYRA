package net.study.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.study.springboot.domain.Employee;
import net.study.springboot.helper.EmployeeModelConverter;
import net.study.springboot.model.EmployeeModel;
import net.study.springboot.service.EmployeeService;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import java.util.zip.DataFormatException;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee employee;
    private EmployeeModel employeeModel;

    @BeforeEach
    public void init() {
        employee = Employee.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .emailId("john.doe@gmail.com")
                .city("New York")
                .state("NY")
                .country("USA")
                .phone("1234567890")
                .dob(LocalDate.of(1990, 5, 15))
                .dateOfJoining(LocalDate.of(2020, 1, 1))
                .salary(new BigDecimal("75000.00"))
                .rating(9)
                .is_active(true)
                .is_deleted(false)
                .employeeHobbies(null)
                .build();

        employeeModel = EmployeeModel.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .emailId("john.doe@gmail.com")
                .city("New York")
                .state("NY")
                .country("USA")
                .phone("1234567890")
                .dob(LocalDate.of(1990, 5, 15))
                .dateOfJoining(LocalDate.of(2020, 1, 1))
                .salary(new BigDecimal("75000.00"))
                .rating(9)
                .employeeHobbies(null)
                .build();
    }

    @Test
    public void createEmployeeTest() throws Exception {
        given(employeeService.addEmployeeService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeModel)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", CoreMatchers.is(employeeModel.getFirstName())))
                .andExpect(jsonPath("$.lastName", CoreMatchers.is(employeeModel.getLastName())))
                .andExpect(jsonPath("$.emailId", CoreMatchers.is(employeeModel.getEmailId())))
                .andExpect(jsonPath("$.city", CoreMatchers.is(employeeModel.getCity())))
                .andExpect(jsonPath("$.state", CoreMatchers.is(employeeModel.getState())))
                .andExpect(jsonPath("$.country", CoreMatchers.is(employeeModel.getCountry())))
                .andExpect(jsonPath("$.phone", CoreMatchers.is(employeeModel.getPhone())))
                .andExpect(jsonPath("$.dob", CoreMatchers.is(employeeModel.getDob().toString())))
                .andExpect(jsonPath("$.dateOfJoining", CoreMatchers.is(employeeModel.getDateOfJoining().toString())))
                .andExpect(jsonPath("$.salary", CoreMatchers.is(employeeModel.getSalary().doubleValue())))
                .andExpect(jsonPath("$.rating", CoreMatchers.is(employeeModel.getRating())));
    }

    @Test
    public void getAllActiveEmployeesTest() throws Exception {
        when(employeeService.getAllActiveEmployeesService()).thenReturn(List.of(employeeModel));

        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", Matchers.is("John")));
    }

    @Test
    public void getAllEmployeesTest() throws Exception {
        when(employeeService.getAllEmployeesService()).thenReturn(List.of(employeeModel));

        mockMvc.perform(get("/api/v1/employees/allEmployee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", Matchers.is("John")));
    }

    @Test
    public void getEmployeeByIdTest() throws Exception {
        int employeeId = 1;
        when(employeeService.getEmployeeByIdService(employeeId)).thenReturn(employeeModel);

        ResultActions response = mockMvc.perform(get("/api/v1/employees/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", CoreMatchers.is(employeeModel.getFirstName())))
                .andExpect(jsonPath("$.lastName", CoreMatchers.is(employeeModel.getLastName())))
                .andExpect(jsonPath("$.emailId", CoreMatchers.is(employeeModel.getEmailId())))
                .andExpect(jsonPath("$.city", CoreMatchers.is(employeeModel.getCity())))
                .andExpect(jsonPath("$.state", CoreMatchers.is(employeeModel.getState())))
                .andExpect(jsonPath("$.country", CoreMatchers.is(employeeModel.getCountry())))
                .andExpect(jsonPath("$.phone", CoreMatchers.is(employeeModel.getPhone())))
                .andExpect(jsonPath("$.dob", CoreMatchers.is(employeeModel.getDob().toString())))
                .andExpect(jsonPath("$.dateOfJoining", CoreMatchers.is(employeeModel.getDateOfJoining().toString())))
                .andExpect(jsonPath("$.salary", CoreMatchers.is(employeeModel.getSalary().doubleValue())))
                .andExpect(jsonPath("$.rating", CoreMatchers.is(employeeModel.getRating())));
    }

    @Test
    public void updateEmployeeTest() throws Exception {
        int employeeId = 1;
        when(employeeService.updateEmployeeService(employeeId, employeeModel)).thenReturn(employeeModel);

        ResultActions response = mockMvc.perform(put("/api/v1/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeModel)));

        response.andExpect(status().isAccepted())
                .andExpect(jsonPath("$.firstName", CoreMatchers.is(employeeModel.getFirstName())))
                .andExpect(jsonPath("$.lastName", CoreMatchers.is(employeeModel.getLastName())))
                .andExpect(jsonPath("$.emailId", CoreMatchers.is(employeeModel.getEmailId())))
                .andExpect(jsonPath("$.city", CoreMatchers.is(employeeModel.getCity())))
                .andExpect(jsonPath("$.state", CoreMatchers.is(employeeModel.getState())))
                .andExpect(jsonPath("$.country", CoreMatchers.is(employeeModel.getCountry())))
                .andExpect(jsonPath("$.phone", CoreMatchers.is(employeeModel.getPhone())))
                .andExpect(jsonPath("$.dob", CoreMatchers.is(employeeModel.getDob().toString())))
                .andExpect(jsonPath("$.dateOfJoining", CoreMatchers.is(employeeModel.getDateOfJoining().toString())))
                .andExpect(jsonPath("$.salary", CoreMatchers.is(employeeModel.getSalary().doubleValue())))
                .andExpect(jsonPath("$.rating", CoreMatchers.is(employeeModel.getRating())));
    }

    @Test
    public void deleteEmployeeTest() throws Exception {
        int employeeId = 1;
        doNothing().when(employeeService).deleteEmployeeService(employeeId);

        ResultActions response = mockMvc.perform(delete("/api/v1/employees/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void addHobbyToEmployeeTest() throws Exception {
        int employeeId = 1;
        int hobbyId = 1;
        doNothing().when(employeeService).addHobbyToEmployeeService(employeeId, hobbyId);

        ResultActions response = mockMvc.perform(post("/api/v1/employees/1/hobbies/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isCreated());
    }

    @Test
    public void removeHobbyFromEmployeeTest() throws Exception {
        int employeeId = 1;
        int hobbyId = 1;
        doNothing().when(employeeService).removeHobbyFromEmployeeService(employeeId, hobbyId);

        ResultActions response = mockMvc.perform(delete("/api/v1/employees/1/hobbies/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());
    }

    @Test
    public void createEmployeeWithInvalidDataTest() throws Exception {
        EmployeeModel invalidEmployee = EmployeeModel.builder()
                .firstName("")
                .lastName("")
                .emailId("invalid-email")
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEmployee)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    public void getEmployeeByIdNotFoundTest() throws Exception {
        int employeeId = 999;
        when(employeeService.getEmployeeByIdService(employeeId)).thenThrow(new RuntimeException("Employee not found"));

        ResultActions response = mockMvc.perform(get("/api/v1/employees/999")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void updateEmployeeNotFoundTest() throws Exception {
        int employeeId = 999;
        when(employeeService.updateEmployeeService(employeeId, employeeModel))
                .thenThrow(new RuntimeException("Employee not found"));

        ResultActions response = mockMvc.perform(put("/api/v1/employees/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeModel)));

        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void deleteEmployeeNotFoundTest() throws Exception {
        int employeeId = 999;
        doThrow(new RuntimeException("Employee not found"))
                .when(employeeService).deleteEmployeeService(employeeId);

        ResultActions response = mockMvc.perform(delete("/api/v1/employees/999")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void addHobbyToEmployeeWithExceptionTest() throws Exception {
        int employeeId = 1;
        int hobbyId = 999;
        doThrow(new RuntimeException("Hobby not found"))
                .when(employeeService).addHobbyToEmployeeService(employeeId, hobbyId);

        ResultActions response = mockMvc.perform(post("/api/v1/employees/1/hobbies/999")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void removeHobbyFromEmployeeWithExceptionTest() throws Exception {
        int employeeId = 999;
        int hobbyId = 1;
        doThrow(new RuntimeException("Employee not found"))
                .when(employeeService).removeHobbyFromEmployeeService(employeeId, hobbyId);

        ResultActions response = mockMvc.perform(delete("/api/v1/employees/999/hobbies/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void createEmployeeWithEmptyListTest() throws Exception {
        List<EmployeeModel> emptyList = List.of();
        when(employeeService.getAllActiveEmployeesService()).thenReturn(emptyList);

        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    public void getAllEmployeesWithEmptyListTest() throws Exception {
        List<EmployeeModel> emptyList = List.of();
        when(employeeService.getAllEmployeesService()).thenReturn(emptyList);

        mockMvc.perform(get("/api/v1/employees/allEmployee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    public void createEmployeeWithNullValuesTest() throws Exception {
        EmployeeModel employeeWithNulls = EmployeeModel.builder()
                .firstName(null)
                .lastName(null)
                .emailId(null)
                .build();

        given(employeeService.addEmployeeService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeWithNulls)));

        response.andExpect(status().isCreated());
    }

    @Test
    public void updateEmployeeWithNullFieldsTest() throws Exception {
        int employeeId = 1;
        EmployeeModel updatedEmployee = EmployeeModel.builder()
                .id(1)
                .firstName("UpdatedName")
                .lastName("UpdatedLastName")
                .emailId("updated@email.com")
                .build();

        when(employeeService.updateEmployeeService(employeeId, updatedEmployee)).thenReturn(updatedEmployee);

        ResultActions response = mockMvc.perform(put("/api/v1/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        response.andExpect(status().isAccepted())
                .andExpect(jsonPath("$.firstName", CoreMatchers.is("UpdatedName")))
                .andExpect(jsonPath("$.lastName", CoreMatchers.is("UpdatedLastName")))
                .andExpect(jsonPath("$.emailId", CoreMatchers.is("updated@email.com")));
    }

    @Test
    public void createEmployeeDataFormatExceptionTest() throws Exception {
        given(employeeService.addEmployeeService(ArgumentMatchers.any()))
                .willThrow(new DataFormatException("Invalid employee data format"));

        ResultActions response = mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeModel)));

        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void updateEmployeeExceptionTest() throws Exception {
        int employeeId = 1;
        when(employeeService.updateEmployeeService(employeeId, employeeModel))
                .thenThrow(new Exception("Database connection failed"));

        ResultActions response = mockMvc.perform(put("/api/v1/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeModel)));

        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void addHobbyToEmployeeExceptionTest() throws Exception {
        int employeeId = 1;
        int hobbyId = 1;
        doNothing().when(employeeService).addHobbyToEmployeeService(employeeId, hobbyId);

        ResultActions response = mockMvc.perform(post("/api/v1/employees/1/hobbies/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isCreated());
    }

    @Test
    public void getEmployeeByIdWithZeroIdTest() throws Exception {
        int employeeId = 0;
        when(employeeService.getEmployeeByIdService(employeeId)).thenReturn(employeeModel);

        ResultActions response = mockMvc.perform(get("/api/v1/employees/0")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk());
    }

    @Test
    public void getEmployeeByIdWithNegativeIdTest() throws Exception {
        int employeeId = -1;
        when(employeeService.getEmployeeByIdService(employeeId)).thenReturn(employeeModel);

        ResultActions response = mockMvc.perform(get("/api/v1/employees/-1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk());
    }

    @Test
    public void createEmployeeWithLargePayloadTest() throws Exception {
        String longString = "A".repeat(1000);
        EmployeeModel largeEmployee = EmployeeModel.builder()
                .firstName(longString)
                .lastName(longString)
                .emailId("large@example.com")
                .city("New York")
                .state("NY")
                .country("USA")
                .phone("1234567890")
                .dob(LocalDate.of(1990, 5, 15))
                .dateOfJoining(LocalDate.of(2020, 1, 1))
                .salary(new BigDecimal("75000.00"))
                .rating(9)
                .build();

        given(employeeService.addEmployeeService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(largeEmployee)));

        response.andExpect(status().isCreated());
    }

    @Test
    public void addHobbyToEmployeeWithInvalidIdsTest() throws Exception {
        doNothing().when(employeeService).addHobbyToEmployeeService(-1, -1);

        ResultActions response = mockMvc.perform(post("/api/v1/employees/-1/hobbies/-1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isCreated());
    }

    @Test
    public void removeHobbyFromEmployeeWithInvalidIdsTest() throws Exception {
        doNothing().when(employeeService).removeHobbyFromEmployeeService(-1, -1);

        ResultActions response = mockMvc.perform(delete("/api/v1/employees/-1/hobbies/-1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());
    }

    @Test
    public void createEmployeeWithMinimalValidDataTest() throws Exception {
        EmployeeModel minimalEmployee = EmployeeModel.builder()
                .firstName("A")
                .lastName("B")
                .emailId("a@b.co")
                .city("NYC")
                .state("NY")
                .country("US")
                .phone("1234567890")
                .dob(LocalDate.of(1990, 1, 1))
                .dateOfJoining(LocalDate.of(2020, 1, 1))
                .salary(new BigDecimal("1"))
                .rating(1)
                .build();

        given(employeeService.addEmployeeService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimalEmployee)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", CoreMatchers.is("A")))
                .andExpect(jsonPath("$.lastName", CoreMatchers.is("B")));
    }

    @Test
    public void createEmployeeWithInvalidEmailFormatTest() throws Exception {
        EmployeeModel invalidEmailEmployee = EmployeeModel.builder()
                .firstName("John")
                .lastName("Doe")
                .emailId("invalid.email")
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEmailEmployee)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    public void createEmployeeWithInvalidPhoneNumberTest() throws Exception {
        EmployeeModel invalidPhoneEmployee = EmployeeModel.builder()
                .firstName("John")
                .lastName("Doe")
                .emailId("john@example.com")
                .phone("invalid_phone")
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPhoneEmployee)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    public void createEmployeeWithInvalidRatingTest() throws Exception {
        EmployeeModel invalidRatingEmployee = EmployeeModel.builder()
                .firstName("John")
                .lastName("Doe")
                .emailId("john@example.com")
                .rating(15) // Rating should be 1-10
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRatingEmployee)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    public void createEmployeeWithNegativeSalaryTest() throws Exception {
        EmployeeModel negativeSalaryEmployee = EmployeeModel.builder()
                .firstName("John")
                .lastName("Doe")
                .emailId("john@example.com")
                .salary(new BigDecimal("-1000"))
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(negativeSalaryEmployee)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    public void createEmployeeWithFutureDateOfBirthTest() throws Exception {
        EmployeeModel futureDobEmployee = EmployeeModel.builder()
                .firstName("John")
                .lastName("Doe")
                .emailId("john@example.com")
                .dob(LocalDate.now().plusYears(1))
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(futureDobEmployee)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    public void createEmployeeWithFutureDateOfJoiningTest() throws Exception {
        EmployeeModel futureDojEmployee = EmployeeModel.builder()
                .firstName("John")
                .lastName("Doe")
                .emailId("john@example.com")
                .dateOfJoining(LocalDate.now().plusYears(1))
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(futureDojEmployee)));

        response.andExpect(status().isBadRequest());
    }
}
