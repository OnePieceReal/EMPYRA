package net.study.springboot.service;

import net.study.springboot.domain.Department;
import net.study.springboot.domain.DepartmentEmployees;
import net.study.springboot.exception.DataFormatException;
import net.study.springboot.exception.ResourceAlreadyExistException;
import net.study.springboot.exception.ResourceNotFoundException;
import net.study.springboot.helper.DepartmentModelConverter;
import net.study.springboot.helper.EmployeeSummary;
import net.study.springboot.model.DepartmentModel;
import net.study.springboot.repository.DepartmentEmployeesRepository;
import net.study.springboot.repository.DepartmentRepository;
import net.study.springboot.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentEmployeesRepository departmentEmployeesRepository;

    @Mock
    private DepartmentModelConverter departmentModelConverter;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private DepartmentModel validDepartmentModel;
    private Department validDepartment;

    @BeforeEach
    void setUp() {
        EmployeeSummary employeeSummary = new EmployeeSummary(1, "John", "Doe");

        validDepartmentModel = DepartmentModel.builder()
                .id(1)
                .name("Engineering")
                .description("Software Development Department")
                .employees(List.of(employeeSummary))
                .build();

        validDepartment = new Department();
        validDepartment.setId(1);
        validDepartment.setName("Engineering");
        validDepartment.setDescription("Software Development Department");
    }

    // Individual Validation Function Tests

    @Test
    void validateAllParameters_validDepartment_returnsTrue() {
        assertTrue(departmentService.validateAllParameters(validDepartmentModel));
    }

    @Test
    void validateAllParameters_nullName_returnsFalse() {
        DepartmentModel deptWithNullName = DepartmentModel.builder()
                .id(1)
                .name(null)
                .description("Valid description")
                .employees(List.of())
                .build();

        assertFalse(departmentService.validateAllParameters(deptWithNullName));
    }

    @Test
    void validateAllParameters_nullDescription_returnsFalse() {
        DepartmentModel deptWithNullDescription = DepartmentModel.builder()
                .id(1)
                .name("Valid name")
                .description(null)
                .employees(List.of())
                .build();

        assertFalse(departmentService.validateAllParameters(deptWithNullDescription));
    }

    @Test
    void validateAllParameters_nullEmployees_returnsFalse() {
        DepartmentModel deptWithNullEmployees = DepartmentModel.builder()
                .id(1)
                .name("Valid name")
                .description("Valid description")
                .employees(null)
                .build();

        assertFalse(departmentService.validateAllParameters(deptWithNullEmployees));
    }

    @Test
    void validateAllParameters_emptyName_returnsFalse() {
        DepartmentModel deptWithEmptyName = DepartmentModel.builder()
                .id(1)
                .name("")
                .description("Valid description")
                .employees(List.of())
                .build();

        assertFalse(departmentService.validateAllParameters(deptWithEmptyName));
    }

    @Test
    void validateAllParameters_emptyDescription_returnsFalse() {
        DepartmentModel deptWithEmptyDescription = DepartmentModel.builder()
                .id(1)
                .name("Valid name")
                .description("")
                .employees(List.of())
                .build();

        assertFalse(departmentService.validateAllParameters(deptWithEmptyDescription));
    }

    @Test
    void validateAllParameters_allFieldsNull_returnsFalse() {
        DepartmentModel deptWithAllNulls = DepartmentModel.builder()
                .id(1)
                .name(null)
                .description(null)
                .employees(null)
                .build();

        assertFalse(departmentService.validateAllParameters(deptWithAllNulls));
    }

    @Test
    void validateAllParameters_allFieldsEmpty_returnsFalse() {
        DepartmentModel deptWithAllEmpty = DepartmentModel.builder()
                .id(1)
                .name("")
                .description("")
                .employees(List.of())
                .build();

        assertFalse(departmentService.validateAllParameters(deptWithAllEmpty));
    }

    @Test
    void validateAllParameters_whitespaceOnlyName_returnsFalse() {
        DepartmentModel deptWithWhitespaceName = DepartmentModel.builder()
                .id(1)
                .name("   ")
                .description("Valid description")
                .employees(List.of())
                .build();

        assertFalse(departmentService.validateAllParameters(deptWithWhitespaceName));
    }

    @Test
    void validateAllParameters_whitespaceOnlyDescription_returnsFalse() {
        DepartmentModel deptWithWhitespaceDescription = DepartmentModel.builder()
                .id(1)
                .name("Valid name")
                .description("   ")
                .employees(List.of())
                .build();

        assertFalse(departmentService.validateAllParameters(deptWithWhitespaceDescription));
    }

    @Test
    void validateAllParameters_singleCharacterFields_returnsTrue() {
        DepartmentModel deptWithSingleChars = DepartmentModel.builder()
                .id(1)
                .name("A")
                .description("B")
                .employees(List.of())
                .build();

        assertTrue(departmentService.validateAllParameters(deptWithSingleChars));
    }

    @Test
    void validateAllParameters_emptyEmployeesList_returnsTrue() {
        DepartmentModel deptWithEmptyEmployees = DepartmentModel.builder()
                .id(1)
                .name("Valid name")
                .description("Valid description")
                .employees(List.of())
                .build();

        assertTrue(departmentService.validateAllParameters(deptWithEmptyEmployees));
    }

    @Test
    void validateAllParameters_specialCharactersInName_returnsTrue() {
        DepartmentModel deptWithSpecialChars = DepartmentModel.builder()
                .id(1)
                .name("R&D Department!")
                .description("Research & Development")
                .employees(List.of())
                .build();

        assertTrue(departmentService.validateAllParameters(deptWithSpecialChars));
    }

    @Test
    void validateAllParameters_veryLongFields_returnsTrue() {
        String longName = "A".repeat(1000);
        String longDescription = "B".repeat(1000);

        DepartmentModel deptWithLongFields = DepartmentModel.builder()
                .id(1)
                .name(longName)
                .description(longDescription)
                .employees(List.of())
                .build();

        assertTrue(departmentService.validateAllParameters(deptWithLongFields));
    }

    // Service Method Tests

    @Test
    void addDepartmentService_validDepartment_success() {
        when(departmentModelConverter.convertDepartmentModelModeltoDepartment(any())).thenReturn(validDepartment);
        when(departmentRepository.existsByName(anyString())).thenReturn(false);
        when(departmentRepository.existsById(anyInt())).thenReturn(false);
        when(departmentRepository.findByName(anyString())).thenReturn(validDepartment);

        DepartmentModel result = departmentService.addDepartmentService(validDepartmentModel);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(departmentRepository).save(validDepartment);
    }

    @Test
    void addDepartmentService_departmentExistsByName_throwsException() {
        when(departmentModelConverter.convertDepartmentModelModeltoDepartment(any())).thenReturn(validDepartment);
        when(departmentRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class,
            () -> departmentService.addDepartmentService(validDepartmentModel));

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void addDepartmentService_departmentExistsById_throwsException() {
        when(departmentModelConverter.convertDepartmentModelModeltoDepartment(any())).thenReturn(validDepartment);
        when(departmentRepository.existsByName(anyString())).thenReturn(false);
        when(departmentRepository.existsById(anyInt())).thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class,
            () -> departmentService.addDepartmentService(validDepartmentModel));

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void addDepartmentService_invalidData_throwsDataFormatException() {
        DepartmentModel invalidDepartment = DepartmentModel.builder()
                .id(1)
                .name("")
                .description("Valid description")
                .employees(List.of())
                .build();

        when(departmentModelConverter.convertDepartmentModelModeltoDepartment(any())).thenReturn(validDepartment);
        when(departmentRepository.existsByName(anyString())).thenReturn(false);
        when(departmentRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(DataFormatException.class,
            () -> departmentService.addDepartmentService(invalidDepartment));

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void getAllDepartmentService_success() {
        List<DepartmentModel> expectedDepartments = Arrays.asList(validDepartmentModel);
        when(departmentRepository.getAllDepartments()).thenReturn(Arrays.asList(validDepartment));
        when(departmentModelConverter.convertDepartmenttoDepartmentModel(any())).thenReturn(validDepartmentModel);

        List<DepartmentModel> result = departmentService.getAllDepartmentService();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(departmentRepository).getAllDepartments();
    }

    @Test
    void getAllDepartmentService_emptyList_returnsEmptyList() {
        when(departmentRepository.getAllDepartments()).thenReturn(Arrays.asList());

        List<DepartmentModel> result = departmentService.getAllDepartmentService();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(departmentRepository).getAllDepartments();
    }

    @Test
    void updateDepartmentService_validUpdate_success() {
        when(departmentRepository.existsById(anyInt())).thenReturn(true);
        when(departmentRepository.existsByNameExcludingId(anyString(), anyInt())).thenReturn(false);
        when(departmentModelConverter.convertDepartmentModelModeltoDepartment(any())).thenReturn(validDepartment);

        DepartmentModel result = departmentService.updateDepartmentService(1, validDepartmentModel);

        assertNotNull(result);
        verify(departmentRepository).save(validDepartment);
    }

    @Test
    void updateDepartmentService_invalidData_throwsDataFormatException() {
        DepartmentModel invalidDepartment = DepartmentModel.builder()
                .id(1)
                .name("")
                .description("Valid description")
                .employees(List.of())
                .build();

        assertThrows(DataFormatException.class,
            () -> departmentService.updateDepartmentService(1, invalidDepartment));

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void updateDepartmentService_idMismatch_throwsDataFormatException() {
        assertThrows(DataFormatException.class,
            () -> departmentService.updateDepartmentService(2, validDepartmentModel));

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void updateDepartmentService_departmentNotFound_throwsResourceNotFoundException() {
        when(departmentRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> departmentService.updateDepartmentService(1, validDepartmentModel));

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void updateDepartmentService_nameAlreadyExists_throwsResourceAlreadyExistException() {
        when(departmentRepository.existsById(anyInt())).thenReturn(true);
        when(departmentRepository.existsByNameExcludingId(anyString(), anyInt())).thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class,
            () -> departmentService.updateDepartmentService(1, validDepartmentModel));

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void deleteDepartmentService_validId_success() {
        when(departmentRepository.existsById(anyInt())).thenReturn(true);

        assertDoesNotThrow(() -> departmentService.deleteDepartmentService(1));

        verify(departmentRepository).deleteById(1);
    }

    @Test
    void deleteDepartmentService_departmentNotFound_throwsException() {
        when(departmentRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> departmentService.deleteDepartmentService(1));

        verify(departmentRepository, never()).deleteById(anyInt());
    }

    @Test
    void addEmployeeToDepartmentService_success() {
        when(employeeRepository.existsById(anyInt())).thenReturn(true);
        when(departmentRepository.existsById(anyInt())).thenReturn(true);
        when(departmentEmployeesRepository.existsEmployeeInDepartment(anyInt(), anyInt())).thenReturn(false);

        assertDoesNotThrow(() -> departmentService.addEmployeeToDepartmentService(1, 1));

        verify(departmentEmployeesRepository).save(any(DepartmentEmployees.class));
    }

    @Test
    void addEmployeeToDepartmentService_employeeNotFound_throwsException() {
        when(employeeRepository.existsById(anyInt())).thenReturn(false);
        when(departmentRepository.existsById(anyInt())).thenReturn(true);

        assertThrows(ResourceNotFoundException.class,
            () -> departmentService.addEmployeeToDepartmentService(1, 1));

        verify(departmentEmployeesRepository, never()).save(any());
    }

    @Test
    void addEmployeeToDepartmentService_departmentNotFound_throwsException() {
        when(employeeRepository.existsById(anyInt())).thenReturn(true);
        when(departmentRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> departmentService.addEmployeeToDepartmentService(1, 1));

        verify(departmentEmployeesRepository, never()).save(any());
    }

    @Test
    void addEmployeeToDepartmentService_bothNotFound_throwsException() {
        when(employeeRepository.existsById(anyInt())).thenReturn(false);
        when(departmentRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> departmentService.addEmployeeToDepartmentService(1, 1));

        verify(departmentEmployeesRepository, never()).save(any());
    }

    @Test
    void addEmployeeToDepartmentService_employeeAlreadyAssigned_throwsException() {
        when(employeeRepository.existsById(anyInt())).thenReturn(true);
        when(departmentRepository.existsById(anyInt())).thenReturn(true);
        when(departmentEmployeesRepository.existsEmployeeInDepartment(anyInt(), anyInt())).thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class,
            () -> departmentService.addEmployeeToDepartmentService(1, 1));

        verify(departmentEmployeesRepository, never()).save(any());
    }

    @Test
    void removeEmployeeFromDepartmentService_success() {
        when(employeeRepository.existsById(anyInt())).thenReturn(true);
        when(departmentRepository.existsById(anyInt())).thenReturn(true);
        when(departmentEmployeesRepository.existsEmployeeInDepartment(anyInt(), anyInt())).thenReturn(true);

        assertDoesNotThrow(() -> departmentService.removeEmployeeFromDepartmentService(1, 1));

        verify(departmentEmployeesRepository).deleteByDepartmentIdAndEmployeeId(1, 1);
    }

    @Test
    void removeEmployeeFromDepartmentService_employeeNotFound_throwsException() {
        when(employeeRepository.existsById(anyInt())).thenReturn(false);
        when(departmentRepository.existsById(anyInt())).thenReturn(true);

        assertThrows(ResourceNotFoundException.class,
            () -> departmentService.removeEmployeeFromDepartmentService(1, 1));

        verify(departmentEmployeesRepository, never()).deleteByDepartmentIdAndEmployeeId(anyInt(), anyInt());
    }

    @Test
    void removeEmployeeFromDepartmentService_departmentNotFound_throwsException() {
        when(employeeRepository.existsById(anyInt())).thenReturn(true);
        when(departmentRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> departmentService.removeEmployeeFromDepartmentService(1, 1));

        verify(departmentEmployeesRepository, never()).deleteByDepartmentIdAndEmployeeId(anyInt(), anyInt());
    }

    @Test
    void removeEmployeeFromDepartmentService_employeeNotAssigned_throwsException() {
        when(employeeRepository.existsById(anyInt())).thenReturn(true);
        when(departmentRepository.existsById(anyInt())).thenReturn(true);
        when(departmentEmployeesRepository.existsEmployeeInDepartment(anyInt(), anyInt())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> departmentService.removeEmployeeFromDepartmentService(1, 1));

        verify(departmentEmployeesRepository, never()).deleteByDepartmentIdAndEmployeeId(anyInt(), anyInt());
    }

    // Edge Cases and Boundary Conditions

    @Test
    void addDepartmentService_withZeroId_success() {
        DepartmentModel deptWithZeroId = DepartmentModel.builder()
                .id(0)
                .name("Test Department")
                .description("Test Description")
                .employees(List.of())
                .build();

        when(departmentModelConverter.convertDepartmentModelModeltoDepartment(any())).thenReturn(validDepartment);
        when(departmentRepository.existsByName(anyString())).thenReturn(false);
        when(departmentRepository.existsById(0)).thenReturn(false);
        when(departmentRepository.findByName(anyString())).thenReturn(validDepartment);

        DepartmentModel result = departmentService.addDepartmentService(deptWithZeroId);

        assertNotNull(result);
        verify(departmentRepository).save(any());
    }

    @Test
    void addDepartmentService_withNegativeId_success() {
        DepartmentModel deptWithNegativeId = DepartmentModel.builder()
                .id(-1)
                .name("Test Department")
                .description("Test Description")
                .employees(List.of())
                .build();

        when(departmentModelConverter.convertDepartmentModelModeltoDepartment(any())).thenReturn(validDepartment);
        when(departmentRepository.existsByName(anyString())).thenReturn(false);
        when(departmentRepository.existsById(-1)).thenReturn(false);
        when(departmentRepository.findByName(anyString())).thenReturn(validDepartment);

        DepartmentModel result = departmentService.addDepartmentService(deptWithNegativeId);

        assertNotNull(result);
        verify(departmentRepository).save(any());
    }

    @Test
    void deleteDepartmentService_withZeroId_throwsException() {
        when(departmentRepository.existsById(0)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> departmentService.deleteDepartmentService(0));

        verify(departmentRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteDepartmentService_withNegativeId_throwsException() {
        when(departmentRepository.existsById(-1)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> departmentService.deleteDepartmentService(-1));

        verify(departmentRepository, never()).deleteById(anyInt());
    }

    @Test
    void addEmployeeToDepartmentService_withZeroIds_throwsException() {
        when(employeeRepository.existsById(0)).thenReturn(false);
        when(departmentRepository.existsById(0)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> departmentService.addEmployeeToDepartmentService(0, 0));

        verify(departmentEmployeesRepository, never()).save(any());
    }

    @Test
    void addEmployeeToDepartmentService_withNegativeIds_throwsException() {
        when(employeeRepository.existsById(-1)).thenReturn(false);
        when(departmentRepository.existsById(-1)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> departmentService.addEmployeeToDepartmentService(-1, -1));

        verify(departmentEmployeesRepository, never()).save(any());
    }

    @Test
    void removeEmployeeFromDepartmentService_withZeroIds_throwsException() {
        when(employeeRepository.existsById(0)).thenReturn(false);
        when(departmentRepository.existsById(0)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> departmentService.removeEmployeeFromDepartmentService(0, 0));

        verify(departmentEmployeesRepository, never()).deleteByDepartmentIdAndEmployeeId(anyInt(), anyInt());
    }

    @Test
    void removeEmployeeFromDepartmentService_withNegativeIds_throwsException() {
        when(employeeRepository.existsById(-1)).thenReturn(false);
        when(departmentRepository.existsById(-1)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> departmentService.removeEmployeeFromDepartmentService(-1, -1));

        verify(departmentEmployeesRepository, never()).deleteByDepartmentIdAndEmployeeId(anyInt(), anyInt());
    }

    @Test
    void updateDepartmentService_withLargeId_throwsException() {
        int largeId = Integer.MAX_VALUE;
        DepartmentModel deptWithLargeId = DepartmentModel.builder()
                .id(largeId)
                .name("Test Department")
                .description("Test Description")
                .employees(List.of())
                .build();

        when(departmentRepository.existsById(largeId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> departmentService.updateDepartmentService(largeId, deptWithLargeId));

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void addDepartmentService_withLargeEmployeeList_success() {
        List<EmployeeSummary> largeEmployeeList = Arrays.asList(
                new EmployeeSummary(1, "Employee1", "LastName1"),
                new EmployeeSummary(2, "Employee2", "LastName2"),
                new EmployeeSummary(3, "Employee3", "LastName3"),
                new EmployeeSummary(4, "Employee4", "LastName4"),
                new EmployeeSummary(5, "Employee5", "LastName5")
        );

        DepartmentModel deptWithManyEmployees = DepartmentModel.builder()
                .id(1)
                .name("Large Department")
                .description("Department with many employees")
                .employees(largeEmployeeList)
                .build();

        when(departmentModelConverter.convertDepartmentModelModeltoDepartment(any())).thenReturn(validDepartment);
        when(departmentRepository.existsByName(anyString())).thenReturn(false);
        when(departmentRepository.existsById(anyInt())).thenReturn(false);
        when(departmentRepository.findByName(anyString())).thenReturn(validDepartment);

        DepartmentModel result = departmentService.addDepartmentService(deptWithManyEmployees);

        assertNotNull(result);
        verify(departmentRepository).save(any());
    }

    @Test
    void getAllDepartmentService_withMultipleDepartments_returnsAll() {
        Department dept1 = new Department();
        dept1.setId(1);
        dept1.setName("Engineering");

        Department dept2 = new Department();
        dept2.setId(2);
        dept2.setName("Marketing");

        DepartmentModel deptModel1 = DepartmentModel.builder()
                .id(1)
                .name("Engineering")
                .employees(List.of())
                .build();

        DepartmentModel deptModel2 = DepartmentModel.builder()
                .id(2)
                .name("Marketing")
                .employees(List.of())
                .build();

        when(departmentRepository.getAllDepartments()).thenReturn(Arrays.asList(dept1, dept2));
        when(departmentModelConverter.convertDepartmenttoDepartmentModel(dept1)).thenReturn(deptModel1);
        when(departmentModelConverter.convertDepartmenttoDepartmentModel(dept2)).thenReturn(deptModel2);

        List<DepartmentModel> result = departmentService.getAllDepartmentService();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Engineering", result.get(0).getName());
        assertEquals("Marketing", result.get(1).getName());
    }

    @Test
    void updateDepartmentService_withZeroId_throwsDataFormatException() {
        DepartmentModel deptWithZeroId = DepartmentModel.builder()
                .id(0)
                .name("Test Department")
                .description("Test Description")
                .employees(List.of())
                .build();

        assertThrows(DataFormatException.class,
            () -> departmentService.updateDepartmentService(1, deptWithZeroId));

        verify(departmentRepository, never()).save(any());
    }

    @Test
    void addDepartmentService_verifyIdIsSetFromRepository() {
        Department savedDepartment = new Department();
        savedDepartment.setId(42);
        savedDepartment.setName("Engineering");

        when(departmentModelConverter.convertDepartmentModelModeltoDepartment(any())).thenReturn(validDepartment);
        when(departmentRepository.existsByName(anyString())).thenReturn(false);
        when(departmentRepository.existsById(anyInt())).thenReturn(false);
        when(departmentRepository.findByName("Engineering")).thenReturn(savedDepartment);

        DepartmentModel result = departmentService.addDepartmentService(validDepartmentModel);

        assertEquals(42, result.getId());
        verify(departmentRepository).findByName("Engineering");
        verify(departmentRepository).save(validDepartment);
    }
}