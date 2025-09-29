package net.study.springboot.service;

import net.study.springboot.domain.Employee;
import net.study.springboot.domain.EmployeeHobbies;
import net.study.springboot.exception.ResourceAlreadyExistException;
import net.study.springboot.exception.ResourceNotFoundException;
import net.study.springboot.helper.EmployeeModelConverter;
import net.study.springboot.model.EmployeeModel;
import net.study.springboot.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeModelConverter employeeModelConverter;

    @Mock
    private EmployeeHobbiesRepository employeeHobbiesRepository;

    @Mock
    private HobbyRepository hobbyRepository;

    @Mock
    private DepartmentEmployeesRepository departmentEmployeesRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeModel validEmployeeModel;
    private Employee validEmployee;

    @BeforeEach
    void setUp() {
        validEmployeeModel = EmployeeModel.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .emailId("john.doe@example.com")
                .phone("1234567890")
                .city("New York")
                .state("NY")
                .country("USA")
                .dob(LocalDate.now().minusYears(25))
                .dateOfJoining(LocalDate.now().minusYears(2))
                .salary(new BigDecimal("60000"))
                .rating(8)
                .build();

        validEmployee = Employee.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .emailId("john.doe@example.com")
                .phone("1234567890")
                .city("New York")
                .state("NY")
                .country("USA")
                .dob(LocalDate.now().minusYears(25))
                .dateOfJoining(LocalDate.now().minusYears(2))
                .salary(new BigDecimal("60000"))
                .rating(8)
                .is_active(true)
                .is_deleted(false)
                .build();
    }

    // Individual Validation Function Tests

    @Test
    void isValidName_validName_returnsTrue() {
        assertTrue(employeeService.isValidName("John"));
        assertTrue(employeeService.isValidName("Mary"));
        assertTrue(employeeService.isValidName("A"));
    }

    @Test
    void isValidName_invalidName_returnsFalse() {
        assertFalse(employeeService.isValidName(null));
        assertFalse(employeeService.isValidName(""));
        assertFalse(employeeService.isValidName("John123"));
        assertFalse(employeeService.isValidName("John-Doe"));
        assertFalse(employeeService.isValidName("John@Doe"));
        assertFalse(employeeService.isValidName("John Doe")); // Space not allowed
    }

    @Test
    void isValidName_nameWithSpecialCharacters_returnsFalse() {
        assertFalse(employeeService.isValidName("John!"));
        assertFalse(employeeService.isValidName("Mary."));
        assertFalse(employeeService.isValidName("A_B"));
        assertFalse(employeeService.isValidName("X&Y"));
    }

    @Test
    void isValidName_nameTooLong_returnsFalse() {
        String longName = "A".repeat(256);
        assertFalse(employeeService.isValidName(longName));
    }

    @Test
    void isValidName_nameExactly255Characters_returnsTrue() {
        String name255 = "A".repeat(255);
        assertTrue(employeeService.isValidName(name255));
    }

    @Test
    void isValidEmail_validEmail_returnsTrue() {
        assertTrue(employeeService.isValidEmail("test@example.com"));
        assertTrue(employeeService.isValidEmail("user.name@domain.co.uk"));
        assertTrue(employeeService.isValidEmail("user+tag@example.org"));
        assertTrue(employeeService.isValidEmail("user_name@example-domain.com"));
    }

    @Test
    void isValidEmail_invalidEmail_returnsFalse() {
        assertFalse(employeeService.isValidEmail(null));
        assertFalse(employeeService.isValidEmail(""));
        assertFalse(employeeService.isValidEmail("invalid-email"));
        assertFalse(employeeService.isValidEmail("@domain.com"));
        assertFalse(employeeService.isValidEmail("user@"));
        assertFalse(employeeService.isValidEmail("user@domain"));
        assertFalse(employeeService.isValidEmail("user.domain.com"));
    }

    @Test
    void isValidEmail_emailWithInvalidTLD_returnsFalse() {
        assertFalse(employeeService.isValidEmail("user@domain.a"));
        assertFalse(employeeService.isValidEmail("user@domain.abcdefgh"));
    }

    @Test
    void isValidPhoneNumber_validPhone_returnsTrue() {
        assertTrue(employeeService.isValidPhoneNumber("1234567890"));
        assertTrue(employeeService.isValidPhoneNumber("12345678901"));
        assertTrue(employeeService.isValidPhoneNumber("123456789012345"));
    }

    @Test
    void isValidPhoneNumber_invalidPhone_returnsFalse() {
        assertFalse(employeeService.isValidPhoneNumber(null));
        assertFalse(employeeService.isValidPhoneNumber(""));
        assertFalse(employeeService.isValidPhoneNumber("123"));
        assertFalse(employeeService.isValidPhoneNumber("123456789"));
        assertFalse(employeeService.isValidPhoneNumber("1234567890123456"));
        assertFalse(employeeService.isValidPhoneNumber("abc1234567"));
        assertFalse(employeeService.isValidPhoneNumber("123-456-7890"));
        assertFalse(employeeService.isValidPhoneNumber("+1234567890"));
    }

    @Test
    void isValidDateOfBirth_validDob_returnsTrue() {
        LocalDate validDob = LocalDate.now().minusYears(25);
        assertTrue(employeeService.isValidDateOfBirth(validDob));

        LocalDate eighteenYearsAgo = LocalDate.now().minusYears(18).minusDays(1);
        assertTrue(employeeService.isValidDateOfBirth(eighteenYearsAgo));
    }

    @Test
    void isValidDateOfBirth_invalidDob_returnsFalse() {
        assertFalse(employeeService.isValidDateOfBirth(null));
        assertFalse(employeeService.isValidDateOfBirth(LocalDate.now().plusDays(1)));
        assertFalse(employeeService.isValidDateOfBirth(LocalDate.now()));
        assertFalse(employeeService.isValidDateOfBirth(LocalDate.now().minusYears(17)));
        assertFalse(employeeService.isValidDateOfBirth(LocalDate.now().minusYears(18)));
    }

    @Test
    void isValidDateOfJoining_validDoj_returnsTrue() {
        LocalDate dob = LocalDate.now().minusYears(25);
        LocalDate doj = LocalDate.now().minusYears(2);
        assertTrue(employeeService.isValidDateOfJoining(doj, dob));

        LocalDate dojMinimum = dob.plusYears(18).plusDays(1);
        assertTrue(employeeService.isValidDateOfJoining(dojMinimum, dob));
    }

    @Test
    void isValidDateOfJoining_invalidDoj_returnsFalse() {
        LocalDate dob = LocalDate.now().minusYears(25);

        assertFalse(employeeService.isValidDateOfJoining(null, dob));
        assertFalse(employeeService.isValidDateOfJoining(LocalDate.now(), null));
        assertFalse(employeeService.isValidDateOfJoining(null, null));
        assertFalse(employeeService.isValidDateOfJoining(LocalDate.now().plusDays(1), dob));
        assertFalse(employeeService.isValidDateOfJoining(dob.plusYears(18), dob));
        assertFalse(employeeService.isValidDateOfJoining(dob.plusYears(17), dob));
    }

    @Test
    void isValidCityOrCountry_validValue_returnsTrue() {
        assertTrue(employeeService.isValidCityOrCountry("New York"));
        assertTrue(employeeService.isValidCityOrCountry("United States"));
        assertTrue(employeeService.isValidCityOrCountry("London"));
        assertTrue(employeeService.isValidCityOrCountry("A"));
        assertTrue(employeeService.isValidCityOrCountry("Los Angeles"));
    }

    @Test
    void isValidCityOrCountry_invalidValue_returnsFalse() {
        assertFalse(employeeService.isValidCityOrCountry(null));
        assertFalse(employeeService.isValidCityOrCountry(""));
        assertFalse(employeeService.isValidCityOrCountry("   "));
        assertFalse(employeeService.isValidCityOrCountry("City123"));
        assertFalse(employeeService.isValidCityOrCountry("City!"));
        assertFalse(employeeService.isValidCityOrCountry("City@Home"));

        String longCity = "A".repeat(256);
        assertFalse(employeeService.isValidCityOrCountry(longCity));
    }

    @Test
    void isValidCityOrCountry_exactlyMaxLength_returnsTrue() {
        String city255 = "A".repeat(255);
        assertTrue(employeeService.isValidCityOrCountry(city255));
    }

    @Test
    void isValidSalary_validSalary_returnsTrue() {
        assertTrue(employeeService.isValidSalary(new BigDecimal("50000")));
        assertTrue(employeeService.isValidSalary(new BigDecimal("0.01")));
        assertTrue(employeeService.isValidSalary(new BigDecimal("999999999.99")));
    }

    @Test
    void isValidSalary_invalidSalary_returnsFalse() {
        assertFalse(employeeService.isValidSalary(null));
        assertFalse(employeeService.isValidSalary(BigDecimal.ZERO));
        assertFalse(employeeService.isValidSalary(new BigDecimal("-1000")));
        assertFalse(employeeService.isValidSalary(new BigDecimal("-0.01")));
    }

    @Test
    void isValidRating_validRating_returnsTrue() {
        assertTrue(employeeService.isValidRating(1));
        assertTrue(employeeService.isValidRating(5));
        assertTrue(employeeService.isValidRating(10));
    }

    @Test
    void isValidRating_invalidRating_returnsFalse() {
        assertFalse(employeeService.isValidRating(0));
        assertFalse(employeeService.isValidRating(11));
        assertFalse(employeeService.isValidRating(-1));
        assertFalse(employeeService.isValidRating(100));
        assertFalse(employeeService.isValidRating(Integer.MAX_VALUE));
        assertFalse(employeeService.isValidRating(Integer.MIN_VALUE));
    }

    // Service Method Tests

    @Test
    void addEmployeeService_validEmployee_success() throws DataFormatException {
        when(employeeModelConverter.convertEmployeeModelToEmployee(any())).thenReturn(validEmployee);
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(employeeRepository.existsByEmailId(any())).thenReturn(false);
        when(employeeRepository.save(any())).thenReturn(validEmployee);
        when(employeeModelConverter.convertEmployeeToEmployeeModel(any())).thenReturn(validEmployeeModel);

        EmployeeModel result = employeeService.addEmployeeService(validEmployeeModel);

        assertNotNull(result);
        verify(employeeRepository).save(any());
        verify(employeeModelConverter).convertEmployeeToEmployeeModel(any());
    }

    @Test
    void addEmployeeService_employeeAlreadyExists_throwsException() {
        when(employeeModelConverter.convertEmployeeModelToEmployee(any())).thenReturn(validEmployee);
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(validEmployee));

        assertThrows(ResourceAlreadyExistException.class,
            () -> employeeService.addEmployeeService(validEmployeeModel));
    }

    @Test
    void addEmployeeService_emailAlreadyExists_throwsException() {
        when(employeeModelConverter.convertEmployeeModelToEmployee(any())).thenReturn(validEmployee);
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(employeeRepository.existsByEmailId(any())).thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class,
            () -> employeeService.addEmployeeService(validEmployeeModel));
    }

    @Test
    void addEmployeeService_invalidData_throwsDataFormatException() {
        EmployeeModel invalidEmployee = EmployeeModel.builder()
                .firstName("")
                .lastName("Doe")
                .emailId("invalid-email")
                .build();

        when(employeeModelConverter.convertEmployeeModelToEmployee(any())).thenReturn(validEmployee);
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(employeeRepository.existsByEmailId(any())).thenReturn(false);

        assertThrows(DataFormatException.class,
            () -> employeeService.addEmployeeService(invalidEmployee));
    }

    @Test
    void updateEmployeeService_validEmployee_success() throws Exception {
        when(employeeRepository.existsById(anyInt())).thenReturn(true);
        when(employeeModelConverter.convertEmployeeModelToEmployee(any())).thenReturn(validEmployee);
        when(employeeRepository.save(any())).thenReturn(validEmployee);
        when(employeeModelConverter.convertEmployeeToEmployeeModel(any())).thenReturn(validEmployeeModel);

        EmployeeModel result = employeeService.updateEmployeeService(1, validEmployeeModel);

        assertNotNull(result);
        verify(employeeRepository).save(any());
    }

    @Test
    void updateEmployeeService_employeeNotFound_throwsException() {
        when(employeeRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> employeeService.updateEmployeeService(1, validEmployeeModel));
    }

    @Test
    void updateEmployeeService_invalidData_throwsDataFormatException() {
        EmployeeModel invalidEmployee = EmployeeModel.builder()
                .id(1)
                .firstName("")
                .build();

        assertThrows(DataFormatException.class,
            () -> employeeService.updateEmployeeService(1, invalidEmployee));
    }

    @Test
    void updateEmployeeService_idMismatch_throwsDataFormatException() {
        assertThrows(DataFormatException.class,
            () -> employeeService.updateEmployeeService(2, validEmployeeModel));
    }

    @Test
    void deleteEmployeeService_validId_success() {
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(validEmployee));
        when(employeeRepository.save(any())).thenReturn(validEmployee);

        assertDoesNotThrow(() -> employeeService.deleteEmployeeService(1));

        verify(employeeRepository).save(any());
        verify(employeeHobbiesRepository).deleteByEmployeeId(1);
        verify(departmentEmployeesRepository).deleteByEmployeeId(1);
    }

    @Test
    void deleteEmployeeService_employeeNotFound_throwsException() {
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> employeeService.deleteEmployeeService(1));
    }

    @Test
    void getAllActiveEmployeesService_success() {
        List<Employee> employees = Arrays.asList(validEmployee);
        when(employeeRepository.getActiveEmployeeList()).thenReturn(employees);
        when(employeeModelConverter.convertEmployeeToEmployeeModel(any())).thenReturn(validEmployeeModel);

        List<EmployeeModel> result = employeeService.getAllActiveEmployeesService();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeeRepository).getActiveEmployeeList();
    }

    @Test
    void getAllActiveEmployeesService_emptyList_returnsEmptyList() {
        when(employeeRepository.getActiveEmployeeList()).thenReturn(Arrays.asList());

        List<EmployeeModel> result = employeeService.getAllActiveEmployeesService();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getEmployeeByIdService_validId_success() {
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(validEmployee));
        when(employeeModelConverter.convertEmployeeToEmployeeModel(any())).thenReturn(validEmployeeModel);

        EmployeeModel result = employeeService.getEmployeeByIdService(1);

        assertNotNull(result);
        verify(employeeRepository).findById(1);
    }

    @Test
    void getEmployeeByIdService_employeeNotFound_throwsException() {
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> employeeService.getEmployeeByIdService(1));
    }

    @Test
    void getAllEmployeesService_success() {
        List<Employee> employees = Arrays.asList(validEmployee);
        when(employeeRepository.getAllEmployeeList()).thenReturn(employees);
        when(employeeModelConverter.convertEmployeeToEmployeeModel(any())).thenReturn(validEmployeeModel);

        List<EmployeeModel> result = employeeService.getAllEmployeesService();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(employeeRepository).getAllEmployeeList();
    }

    @Test
    void addHobbyToEmployeeService_success() throws Exception {
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(validEmployee));
        when(hobbyRepository.existsById(anyInt())).thenReturn(true);
        when(employeeHobbiesRepository.existsByEmployeeIdAndHobbyId(anyInt(), anyInt())).thenReturn(false);

        assertDoesNotThrow(() -> employeeService.addHobbyToEmployeeService(1, 1));

        verify(employeeHobbiesRepository).save(any());
    }

    @Test
    void addHobbyToEmployeeService_employeeNotFound_throwsException() {
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> employeeService.addHobbyToEmployeeService(1, 1));
    }

    @Test
    void addHobbyToEmployeeService_employeeDeleted_throwsException() {
        Employee deletedEmployee = Employee.builder()
                .id(1)
                .is_deleted(true)
                .build();
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(deletedEmployee));

        assertThrows(ResourceNotFoundException.class,
            () -> employeeService.addHobbyToEmployeeService(1, 1));
    }

    @Test
    void addHobbyToEmployeeService_hobbyNotFound_throwsException() {
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(validEmployee));
        when(hobbyRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> employeeService.addHobbyToEmployeeService(1, 1));
    }

    @Test
    void addHobbyToEmployeeService_hobbyAlreadyAssigned_throwsException() {
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.of(validEmployee));
        when(hobbyRepository.existsById(anyInt())).thenReturn(true);
        when(employeeHobbiesRepository.existsByEmployeeIdAndHobbyId(anyInt(), anyInt())).thenReturn(true);

        assertThrows(ResourceAlreadyExistException.class,
            () -> employeeService.addHobbyToEmployeeService(1, 1));
    }

    @Test
    void removeHobbyFromEmployeeService_success() {
        when(employeeHobbiesRepository.existsByEmployeeIdAndHobbyId(anyInt(), anyInt())).thenReturn(true);

        assertDoesNotThrow(() -> employeeService.removeHobbyFromEmployeeService(1, 1));

        verify(employeeHobbiesRepository).deleteByEmployeeIdAndHobbyId(1, 1);
    }

    @Test
    void removeHobbyFromEmployeeService_hobbyNotAssigned_throwsException() {
        when(employeeHobbiesRepository.existsByEmployeeIdAndHobbyId(anyInt(), anyInt())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> employeeService.removeHobbyFromEmployeeService(1, 1));
    }

    // Edge Cases and Boundary Conditions

    @Test
    void addEmployeeService_withNullFields_throwsDataFormatException() {
        EmployeeModel employeeWithNulls = EmployeeModel.builder()
                .firstName(null)
                .lastName("Doe")
                .emailId("test@example.com")
                .build();

        when(employeeModelConverter.convertEmployeeModelToEmployee(any())).thenReturn(validEmployee);
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(employeeRepository.existsByEmailId(any())).thenReturn(false);

        assertThrows(DataFormatException.class,
            () -> employeeService.addEmployeeService(employeeWithNulls));
    }

    @Test
    void isValidPhoneNumber_boundaryConditions() {
        assertTrue(employeeService.isValidPhoneNumber("1234567890")); // 10 digits
        assertTrue(employeeService.isValidPhoneNumber("123456789012345")); // 15 digits
        assertFalse(employeeService.isValidPhoneNumber("123456789")); // 9 digits
        assertFalse(employeeService.isValidPhoneNumber("1234567890123456")); // 16 digits
    }

    @Test
    void isValidRating_boundaryConditions() {
        assertTrue(employeeService.isValidRating(1)); // minimum
        assertTrue(employeeService.isValidRating(10)); // maximum
        assertFalse(employeeService.isValidRating(0)); // below minimum
        assertFalse(employeeService.isValidRating(11)); // above maximum
    }

    @Test
    void isValidDateOfBirth_boundaryConditions() {
        LocalDate today = LocalDate.now();
        LocalDate exactly18YearsAgo = today.minusYears(18);
        LocalDate just19YearsAgo = today.minusYears(18).minusDays(1);

        assertFalse(employeeService.isValidDateOfBirth(exactly18YearsAgo));
        assertTrue(employeeService.isValidDateOfBirth(just19YearsAgo));
        assertFalse(employeeService.isValidDateOfBirth(today));
        assertFalse(employeeService.isValidDateOfBirth(today.plusDays(1)));
    }

    @Test
    void addHobbyToEmployeeService_withZeroIds_throwsException() {
        when(employeeRepository.findById(0)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> employeeService.addHobbyToEmployeeService(0, 0));
    }

    @Test
    void addHobbyToEmployeeService_withNegativeIds_throwsException() {
        when(employeeRepository.findById(-1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> employeeService.addHobbyToEmployeeService(-1, -1));
    }

    @Test
    void removeHobbyFromEmployeeService_withZeroIds_throwsException() {
        when(employeeHobbiesRepository.existsByEmployeeIdAndHobbyId(0, 0)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
            () -> employeeService.removeHobbyFromEmployeeService(0, 0));
    }

    @Test
    void isValidSalary_extremeValues() {
        assertTrue(employeeService.isValidSalary(new BigDecimal("0.000001")));
        assertFalse(employeeService.isValidSalary(new BigDecimal("0")));
        assertFalse(employeeService.isValidSalary(new BigDecimal("-0.000001")));
    }

    @Test
    void getAllEmployeesService_emptyList_returnsEmptyList() {
        when(employeeRepository.getAllEmployeeList()).thenReturn(Arrays.asList());

        List<EmployeeModel> result = employeeService.getAllEmployeesService();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository).getAllEmployeeList();
    }

    @Test
    void deleteEmployeeService_verifySoftDelete_setsIsDeletedTrue() {
        Employee employeeToDelete = Employee.builder()
                .id(1)
                .firstName("John")
                .is_deleted(false)
                .build();

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employeeToDelete));

        employeeService.deleteEmployeeService(1);

        assertTrue(employeeToDelete.is_deleted());
        verify(employeeRepository).save(employeeToDelete);
        verify(employeeHobbiesRepository).deleteByEmployeeId(1);
        verify(departmentEmployeesRepository).deleteByEmployeeId(1);
    }

    @Test
    void updateEmployeeService_verifiesIsActiveAndIsDeletedAreSet() throws Exception {
        when(employeeRepository.existsById(1)).thenReturn(true);
        when(employeeModelConverter.convertEmployeeModelToEmployee(validEmployeeModel)).thenReturn(validEmployee);
        when(employeeRepository.save(any())).thenReturn(validEmployee);
        when(employeeModelConverter.convertEmployeeToEmployeeModel(any())).thenReturn(validEmployeeModel);

        employeeService.updateEmployeeService(1, validEmployeeModel);

        assertTrue(validEmployee.is_active());
        assertFalse(validEmployee.is_deleted());
        verify(employeeRepository).save(validEmployee);
    }
}