package net.study.springboot.service;

import net.study.springboot.model.EmployeeModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

class EmployeeServiceTest {

    private EmployeeService employeeService;
    private EmployeeModel validEmployee;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService();
        validEmployee = EmployeeModel.builder()
                .firstName("James")
                .lastName("Smith")
                .emailId("james.smith@example.com")
                .phone("1234567890")
                .city("New York")
                .country("USA")
                .dob(LocalDate.now().minusYears(25))
                .dateOfJoining(LocalDate.now().minusYears(2))
                .salary(new BigDecimal("60000"))
                .rating(8)
                .build();
    }

    @Test
    void validateAllParameters_validEmployee_returnsTrue() {
        Assertions.assertTrue(employeeService.validateAllParameters(validEmployee));
    }

    @Test
    void validateAllParameters_invalidEmail_returnsFalse() {
        validEmployee.setEmailId("invalid-email");
        Assertions.assertFalse(employeeService.validateAllParameters(validEmployee));
    }

    @Test
    void validateAllParameters_invalidPhone_returnsFalse() {
        validEmployee.setPhone("abc123");
        Assertions.assertFalse(employeeService.validateAllParameters(validEmployee));
    }

    @Test
    void validateAllParameters_youngDob_returnsFalse() {
        validEmployee.setDob(LocalDate.now().minusYears(10));
        Assertions.assertFalse(employeeService.validateAllParameters(validEmployee));
    }

    @Test
    void validateAllParameters_futureJoinDate_returnsFalse() {
        validEmployee.setDateOfJoining(LocalDate.now().plusDays(1));
        Assertions.assertFalse(employeeService.validateAllParameters(validEmployee));
    }

    @Test
    void validateAllParameters_invalidRating_returnsFalse() {
        validEmployee.setRating(15);
        Assertions.assertFalse(employeeService.validateAllParameters(validEmployee));
    }

    @Test
    void validateAllParameters_invalidSalary_returnsFalse() {
        validEmployee.setSalary(BigDecimal.ZERO);
        Assertions.assertFalse(employeeService.validateAllParameters(validEmployee));
    }

    @Test
    void validateAllParameters_invalidCity_returnsFalse() {
        validEmployee.setCity("Toronto123");
        Assertions.assertFalse(employeeService.validateAllParameters(validEmployee));
    }

    @Test
    void validateAllParameters_invalidCountry_returnsFalse() {
        validEmployee.setCountry("CA!");
        Assertions.assertFalse(employeeService.validateAllParameters(validEmployee));
    }
}
