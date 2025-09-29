package net.study.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.study.springboot.helper.EmployeeSummary;
import net.study.springboot.model.DepartmentModel;
import net.study.springboot.service.DepartmentService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.zip.DataFormatException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepartmentController.class)
@ExtendWith(MockitoExtension.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private DepartmentModel departmentModel;
    private EmployeeSummary employeeSummary;

    @BeforeEach
    public void init() {
        employeeSummary = new EmployeeSummary(1, "John", "Doe");

        departmentModel = DepartmentModel.builder()
                .id(1)
                .name("Engineering")
                .description("Software Development Department")
                .employees(List.of(employeeSummary))
                .build();
    }

    @Test
    public void createDepartmentTest() throws Exception {
        given(departmentService.addDepartmentService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentModel)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", CoreMatchers.is(departmentModel.getName())))
                .andExpect(jsonPath("$.description", CoreMatchers.is(departmentModel.getDescription())))
                .andExpect(jsonPath("$.employees", Matchers.hasSize(1)));
    }

    @Test
    public void getAllDepartmentsTest() throws Exception {
        when(departmentService.getAllDepartmentService()).thenReturn(List.of(departmentModel));

        mockMvc.perform(get("/api/v1/department/allDepartment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].name", Matchers.is("Engineering")))
                .andExpect(jsonPath("$[0].description", Matchers.is("Software Development Department")));
    }

    @Test
    public void updateDepartmentTest() throws Exception {
        int departmentId = 1;
        DepartmentModel updatedDepartment = DepartmentModel.builder()
                .id(1)
                .name("Updated Engineering")
                .description("Updated Software Development Department")
                .employees(List.of(employeeSummary))
                .build();

        when(departmentService.updateDepartmentService(departmentId, updatedDepartment)).thenReturn(updatedDepartment);

        ResultActions response = mockMvc.perform(put("/api/v1/department/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDepartment)));

        response.andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name", CoreMatchers.is("Updated Engineering")))
                .andExpect(jsonPath("$.description", CoreMatchers.is("Updated Software Development Department")));
    }

    @Test
    public void deleteDepartmentTest() throws Exception {
        int departmentId = 1;
        doNothing().when(departmentService).deleteDepartmentService(departmentId);

        ResultActions response = mockMvc.perform(delete("/api/v1/department/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void addEmployeeToDepartmentTest() throws Exception {
        int departmentId = 1;
        int employeeId = 1;
        doNothing().when(departmentService).addEmployeeToDepartmentService(departmentId, employeeId);

        ResultActions response = mockMvc.perform(post("/api/v1/department/1/employees/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isCreated());
    }

    @Test
    public void removeEmployeeFromDepartmentTest() throws Exception {
        int departmentId = 1;
        int employeeId = 1;
        doNothing().when(departmentService).removeEmployeeFromDepartmentService(departmentId, employeeId);

        ResultActions response = mockMvc.perform(delete("/api/v1/department/1/employees/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());
    }

    @Test
    public void createDepartmentDataFormatExceptionTest() throws Exception {
        given(departmentService.addDepartmentService(ArgumentMatchers.any()))
                .willThrow(new DataFormatException("Invalid department data format"));

        ResultActions response = mockMvc.perform(post("/api/v1/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentModel)));

        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void updateDepartmentExceptionTest() throws Exception {
        int departmentId = 1;
        when(departmentService.updateDepartmentService(departmentId, departmentModel))
                .willThrow(new Exception("Database connection failed"));

        ResultActions response = mockMvc.perform(put("/api/v1/department/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentModel)));

        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void createDepartmentWithInvalidDataTest() throws Exception {
        DepartmentModel invalidDepartment = DepartmentModel.builder()
                .name("")
                .description("")
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDepartment)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    public void getAllDepartmentsWithEmptyListTest() throws Exception {
        List<DepartmentModel> emptyList = List.of();
        when(departmentService.getAllDepartmentService()).thenReturn(emptyList);

        mockMvc.perform(get("/api/v1/department/allDepartment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    public void getAllDepartmentsWithMultipleDepartmentsTest() throws Exception {
        DepartmentModel dept2 = DepartmentModel.builder()
                .id(2)
                .name("Marketing")
                .description("Marketing and Sales Department")
                .employees(List.of())
                .build();

        when(departmentService.getAllDepartmentService()).thenReturn(List.of(departmentModel, dept2));

        mockMvc.perform(get("/api/v1/department/allDepartment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].name", Matchers.is("Engineering")))
                .andExpect(jsonPath("$[1].name", Matchers.is("Marketing")));
    }

    @Test
    public void createDepartmentWithoutEmployeesTest() throws Exception {
        DepartmentModel deptWithoutEmployees = DepartmentModel.builder()
                .id(2)
                .name("HR")
                .description("Human Resources Department")
                .employees(List.of())
                .build();

        given(departmentService.addDepartmentService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deptWithoutEmployees)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", CoreMatchers.is("HR")))
                .andExpect(jsonPath("$.employees", Matchers.hasSize(0)));
    }

    @Test
    public void addEmployeeToDepartmentWithInvalidIdsTest() throws Exception {
        doNothing().when(departmentService).addEmployeeToDepartmentService(-1, -1);

        ResultActions response = mockMvc.perform(post("/api/v1/department/-1/employees/-1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isCreated());
    }

    @Test
    public void removeEmployeeFromDepartmentWithInvalidIdsTest() throws Exception {
        doNothing().when(departmentService).removeEmployeeFromDepartmentService(-1, -1);

        ResultActions response = mockMvc.perform(delete("/api/v1/department/-1/employees/-1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());
    }

    @Test
    public void updateDepartmentNotFoundTest() throws Exception {
        int departmentId = 999;
        when(departmentService.updateDepartmentService(departmentId, departmentModel))
                .willThrow(new RuntimeException("Department not found"));

        ResultActions response = mockMvc.perform(put("/api/v1/department/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentModel)));

        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void deleteDepartmentNotFoundTest() throws Exception {
        int departmentId = 999;
        doNothing().when(departmentService).deleteDepartmentService(departmentId);

        ResultActions response = mockMvc.perform(delete("/api/v1/department/999")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());
    }

    @Test
    public void createDepartmentWithLongDescriptionTest() throws Exception {
        String longDescription = "A".repeat(1000);
        DepartmentModel longDescDept = DepartmentModel.builder()
                .id(3)
                .name("Research")
                .description(longDescription)
                .employees(List.of())
                .build();

        given(departmentService.addDepartmentService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longDescDept)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", CoreMatchers.is("Research")));
    }

    @Test
    public void createDepartmentWithNullValuesTest() throws Exception {
        DepartmentModel nullDept = DepartmentModel.builder()
                .name(null)
                .description(null)
                .employees(null)
                .build();

        given(departmentService.addDepartmentService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullDept)));

        response.andExpect(status().isCreated());
    }

    @Test
    public void updateDepartmentWithMinimalDataTest() throws Exception {
        int departmentId = 1;
        DepartmentModel minimalDept = DepartmentModel.builder()
                .id(1)
                .name("IT")
                .description("IT")
                .employees(List.of())
                .build();

        when(departmentService.updateDepartmentService(departmentId, minimalDept)).thenReturn(minimalDept);

        ResultActions response = mockMvc.perform(put("/api/v1/department/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimalDept)));

        response.andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name", CoreMatchers.is("IT")))
                .andExpect(jsonPath("$.description", CoreMatchers.is("IT")));
    }

    @Test
    public void updateDepartmentWithZeroIdTest() throws Exception {
        int departmentId = 0;
        when(departmentService.updateDepartmentService(departmentId, departmentModel)).thenReturn(departmentModel);

        ResultActions response = mockMvc.perform(put("/api/v1/department/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentModel)));

        response.andExpect(status().isAccepted());
    }

    @Test
    public void updateDepartmentWithNegativeIdTest() throws Exception {
        int departmentId = -1;
        when(departmentService.updateDepartmentService(departmentId, departmentModel)).thenReturn(departmentModel);

        ResultActions response = mockMvc.perform(put("/api/v1/department/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentModel)));

        response.andExpect(status().isAccepted());
    }

    @Test
    public void deleteDepartmentWithZeroIdTest() throws Exception {
        int departmentId = 0;
        doNothing().when(departmentService).deleteDepartmentService(departmentId);

        ResultActions response = mockMvc.perform(delete("/api/v1/department/0")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());
    }

    @Test
    public void deleteDepartmentWithNegativeIdTest() throws Exception {
        int departmentId = -1;
        doNothing().when(departmentService).deleteDepartmentService(departmentId);

        ResultActions response = mockMvc.perform(delete("/api/v1/department/-1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());
    }

    @Test
    public void createDepartmentWithSpecialCharactersTest() throws Exception {
        DepartmentModel specialCharDept = DepartmentModel.builder()
                .id(4)
                .name("R&D")
                .description("Research & Development Department!")
                .employees(List.of())
                .build();

        given(departmentService.addDepartmentService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialCharDept)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", CoreMatchers.is("R&D")))
                .andExpect(jsonPath("$.description", CoreMatchers.is("Research & Development Department!")));
    }

    @Test
    public void createDepartmentWithVeryLongNameTest() throws Exception {
        String longName = "A".repeat(500);
        DepartmentModel longNameDept = DepartmentModel.builder()
                .id(5)
                .name(longName)
                .description("Department with very long name")
                .employees(List.of())
                .build();

        given(departmentService.addDepartmentService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longNameDept)));

        response.andExpect(status().isCreated());
    }

    @Test
    public void createDepartmentWithMinimalDataTest() throws Exception {
        DepartmentModel minimalDept = DepartmentModel.builder()
                .id(6)
                .name("A")
                .description("B")
                .employees(List.of())
                .build();

        given(departmentService.addDepartmentService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimalDept)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", CoreMatchers.is("A")))
                .andExpect(jsonPath("$.description", CoreMatchers.is("B")));
    }

    @Test
    public void updateDepartmentWithNullFieldsTest() throws Exception {
        int departmentId = 1;
        DepartmentModel nullFieldsDept = DepartmentModel.builder()
                .id(1)
                .name("Updated Name")
                .description(null)
                .employees(null)
                .build();

        when(departmentService.updateDepartmentService(departmentId, nullFieldsDept)).thenReturn(nullFieldsDept);

        ResultActions response = mockMvc.perform(put("/api/v1/department/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullFieldsDept)));

        response.andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name", CoreMatchers.is("Updated Name")));
    }

    @Test
    public void updateDepartmentWithCompletelyDifferentDataTest() throws Exception {
        int departmentId = 1;
        DepartmentModel completeDifferentDept = DepartmentModel.builder()
                .id(1)
                .name("Finance")
                .description("Financial Operations Department")
                .employees(List.of(new EmployeeSummary(2, "Jane", "Smith")))
                .build();

        when(departmentService.updateDepartmentService(departmentId, completeDifferentDept)).thenReturn(completeDifferentDept);

        ResultActions response = mockMvc.perform(put("/api/v1/department/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(completeDifferentDept)));

        response.andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name", CoreMatchers.is("Finance")))
                .andExpect(jsonPath("$.description", CoreMatchers.is("Financial Operations Department")));
    }

    @Test
    public void addEmployeeToDepartmentExceptionTest() throws Exception {
        int departmentId = 1;
        int employeeId = 1;
        doNothing().when(departmentService).addEmployeeToDepartmentService(departmentId, employeeId);

        ResultActions response = mockMvc.perform(post("/api/v1/department/1/employees/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isCreated());
    }

    @Test
    public void removeEmployeeFromDepartmentExceptionTest() throws Exception {
        int departmentId = 1;
        int employeeId = 1;
        doNothing().when(departmentService).removeEmployeeFromDepartmentService(departmentId, employeeId);

        ResultActions response = mockMvc.perform(delete("/api/v1/department/1/employees/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());
    }

    @Test
    public void addEmployeeToDepartmentNotFoundTest() throws Exception {
        int departmentId = 999;
        int employeeId = 999;
        doNothing().when(departmentService).addEmployeeToDepartmentService(departmentId, employeeId);

        ResultActions response = mockMvc.perform(post("/api/v1/department/999/employees/999")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isCreated());
    }

    @Test
    public void removeEmployeeFromDepartmentNotFoundTest() throws Exception {
        int departmentId = 999;
        int employeeId = 999;
        doNothing().when(departmentService).removeEmployeeFromDepartmentService(departmentId, employeeId);

        ResultActions response = mockMvc.perform(delete("/api/v1/department/999/employees/999")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());
    }

    @Test
    public void addEmployeeToDepartmentWithZeroIdsTest() throws Exception {
        doNothing().when(departmentService).addEmployeeToDepartmentService(0, 0);

        ResultActions response = mockMvc.perform(post("/api/v1/department/0/employees/0")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isCreated());
    }

    @Test
    public void removeEmployeeFromDepartmentWithZeroIdsTest() throws Exception {
        doNothing().when(departmentService).removeEmployeeFromDepartmentService(0, 0);

        ResultActions response = mockMvc.perform(delete("/api/v1/department/0/employees/0")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());
    }

    @Test
    public void createDepartmentWithMultipleEmployeesTest() throws Exception {
        EmployeeSummary emp1 = new EmployeeSummary(1, "John", "Doe");
        EmployeeSummary emp2 = new EmployeeSummary(2, "Jane", "Smith");
        EmployeeSummary emp3 = new EmployeeSummary(3, "Bob", "Johnson");

        DepartmentModel multiEmpDept = DepartmentModel.builder()
                .id(7)
                .name("Operations")
                .description("Operations Department")
                .employees(List.of(emp1, emp2, emp3))
                .build();

        given(departmentService.addDepartmentService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(multiEmpDept)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", CoreMatchers.is("Operations")))
                .andExpect(jsonPath("$.employees", Matchers.hasSize(3)));
    }

    @Test
    public void getAllDepartmentsWithMultipleEmployeesTest() throws Exception {
        EmployeeSummary emp1 = new EmployeeSummary(1, "John", "Doe");
        EmployeeSummary emp2 = new EmployeeSummary(2, "Jane", "Smith");

        DepartmentModel dept1 = DepartmentModel.builder()
                .id(1)
                .name("Engineering")
                .description("Software Development")
                .employees(List.of(emp1, emp2))
                .build();

        DepartmentModel dept2 = DepartmentModel.builder()
                .id(2)
                .name("Marketing")
                .description("Marketing Department")
                .employees(List.of())
                .build();

        when(departmentService.getAllDepartmentService()).thenReturn(List.of(dept1, dept2));

        mockMvc.perform(get("/api/v1/department/allDepartment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].employees", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[1].employees", Matchers.hasSize(0)));
    }

    @Test
    public void getAllDepartmentsServiceExceptionTest() throws Exception {
        when(departmentService.getAllDepartmentService()).thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/api/v1/department/allDepartment"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void deleteDepartmentServiceExceptionTest() throws Exception {
        int departmentId = 1;
        doNothing().when(departmentService).deleteDepartmentService(departmentId);

        ResultActions response = mockMvc.perform(delete("/api/v1/department/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNoContent());
    }

    @Test
    public void addEmployeeToDepartmentServiceExceptionTest() throws Exception {
        int departmentId = 1;
        int employeeId = 1;
        doNothing().when(departmentService).addEmployeeToDepartmentService(departmentId, employeeId);

        ResultActions response = mockMvc.perform(post("/api/v1/department/1/employees/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isCreated());
    }

    @Test
    public void createDepartmentWithNumericCharactersTest() throws Exception {
        DepartmentModel numericDept = DepartmentModel.builder()
                .id(8)
                .name("Office 365 Support")
                .description("Support for Office 365 applications and services")
                .employees(List.of())
                .build();

        given(departmentService.addDepartmentService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(numericDept)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", CoreMatchers.is("Office 365 Support")));
    }

    @Test
    public void updateDepartmentDataFormatExceptionTest() throws Exception {
        int departmentId = 1;
        when(departmentService.updateDepartmentService(departmentId, departmentModel))
                .willThrow(new DataFormatException("Invalid update data format"));

        ResultActions response = mockMvc.perform(put("/api/v1/department/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentModel)));

        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void updateDepartmentWithEmptyNameTest() throws Exception {
        int departmentId = 1;
        DepartmentModel emptyNameDept = DepartmentModel.builder()
                .id(1)
                .name("")
                .description("Department with empty name")
                .employees(List.of())
                .build();

        when(departmentService.updateDepartmentService(departmentId, emptyNameDept)).thenReturn(emptyNameDept);

        ResultActions response = mockMvc.perform(put("/api/v1/department/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyNameDept)));

        response.andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name", CoreMatchers.is("")));
    }

    @Test
    public void updateDepartmentWithNullEmployeesTest() throws Exception {
        int departmentId = 1;
        DepartmentModel nullEmpDept = DepartmentModel.builder()
                .id(1)
                .name("Updated Department")
                .description("Updated description")
                .employees(null)
                .build();

        when(departmentService.updateDepartmentService(departmentId, nullEmpDept)).thenReturn(nullEmpDept);

        ResultActions response = mockMvc.perform(put("/api/v1/department/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullEmpDept)));

        response.andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name", CoreMatchers.is("Updated Department")));
    }

    @Test
    public void createDepartmentWithLargeEmployeeListTest() throws Exception {
        List<EmployeeSummary> largeEmployeeList = List.of(
                new EmployeeSummary(1, "Employee1", "LastName1"),
                new EmployeeSummary(2, "Employee2", "LastName2"),
                new EmployeeSummary(3, "Employee3", "LastName3"),
                new EmployeeSummary(4, "Employee4", "LastName4"),
                new EmployeeSummary(5, "Employee5", "LastName5"),
                new EmployeeSummary(6, "Employee6", "LastName6"),
                new EmployeeSummary(7, "Employee7", "LastName7"),
                new EmployeeSummary(8, "Employee8", "LastName8"),
                new EmployeeSummary(9, "Employee9", "LastName9"),
                new EmployeeSummary(10, "Employee10", "LastName10")
        );

        DepartmentModel largeDept = DepartmentModel.builder()
                .id(9)
                .name("Large Department")
                .description("Department with many employees")
                .employees(largeEmployeeList)
                .build();

        given(departmentService.addDepartmentService(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/v1/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(largeDept)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", CoreMatchers.is("Large Department")))
                .andExpect(jsonPath("$.employees", Matchers.hasSize(10)));
    }

    @Test
    public void createDepartmentWithDuplicateNameTest() throws Exception {
        given(departmentService.addDepartmentService(ArgumentMatchers.any()))
                .willThrow(new RuntimeException("Department name already exists"));

        ResultActions response = mockMvc.perform(post("/api/v1/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentModel)));

        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void createDepartmentWithExcessivelyLongNameTest() throws Exception {
        String excessivelyLongName = "A".repeat(10000);
        DepartmentModel longNameDept = DepartmentModel.builder()
                .name(excessivelyLongName)
                .description("Test department")
                .employees(List.of())
                .build();

        ResultActions response = mockMvc.perform(post("/api/v1/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longNameDept)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    public void addEmployeeToDepartmentWithSameEmployeeTwiceTest() throws Exception {
        int departmentId = 1;
        int employeeId = 1;
        doThrow(new RuntimeException("Employee already assigned to department"))
                .when(departmentService).addEmployeeToDepartmentService(departmentId, employeeId);

        ResultActions response = mockMvc.perform(post("/api/v1/department/1/employees/1")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isInternalServerError());
    }

    @Test
    public void updateDepartmentWithInvalidIdBoundaryTest() throws Exception {
        int departmentId = Integer.MAX_VALUE;
        when(departmentService.updateDepartmentService(departmentId, departmentModel))
                .thenThrow(new RuntimeException("Invalid department ID range"));

        ResultActions response = mockMvc.perform(put("/api/v1/department/" + departmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentModel)));

        response.andExpect(status().isInternalServerError());
    }
}