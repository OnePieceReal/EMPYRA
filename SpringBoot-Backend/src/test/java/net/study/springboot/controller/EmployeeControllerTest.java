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

import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

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

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EmployeeController.class)
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
                .firstName("mullooor")
                .lastName("idrc")
                .emailId("hello@gmail.com")
                .city("missisippi")
                .state("MS")
                .country("USA")
                .phone("1147484364")
                .dob(LocalDate.of(1995, 1, 1))
                .dateOfJoining(LocalDate.of(2020, 1, 1))
                .salary(new BigDecimal("50000.00"))
                .rating(8)
                .is_active(true)
                .is_deleted(false)
                .employeeHobbies(null)
                .build();

        employeeModel = EmployeeModel.builder()
                .id(1)
                .firstName("mullooor")
                .lastName("idrc")
                .emailId("hello@gmail.com")
                .city("missisippi")
                .state("MS")
                .country("USA")
                .phone("1147484364")
                .dob(LocalDate.of(1995, 1, 1))
                .dateOfJoining(LocalDate.of(2020, 1, 1))
                .salary(new BigDecimal("50000.00"))
                .rating(8)
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
                .andExpect(jsonPath("$.phone", CoreMatchers.is(employeeModel.getPhone())))
                .andExpect(jsonPath("$.salary", CoreMatchers.is(employeeModel.getSalary().intValue())))
                .andExpect(jsonPath("$.rating", CoreMatchers.is(employeeModel.getRating())));
    }

    @Test
    public void getAllActiveEmployeesTest() throws Exception {
        when(employeeService.getAllActiveEmployeesService()).thenReturn(List.of(employeeModel));

        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", Matchers.is("mullooor")));
    }

    @Test
    public void getAllEmployeesTest() throws Exception {
        when(employeeService.getAllEmployeesService()).thenReturn(List.of(employeeModel));

        mockMvc.perform(get("/api/v1/employees/allEmployee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", Matchers.is("mullooor")));
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
                .andExpect(jsonPath("$.phone", CoreMatchers.is(employeeModel.getPhone())))
                .andExpect(jsonPath("$.salary", CoreMatchers.is(employeeModel.getSalary().intValue())))
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
                .andExpect(jsonPath("$.phone", CoreMatchers.is(employeeModel.getPhone())))
                .andExpect(jsonPath("$.salary", CoreMatchers.is(employeeModel.getSalary().intValue())))
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
}
