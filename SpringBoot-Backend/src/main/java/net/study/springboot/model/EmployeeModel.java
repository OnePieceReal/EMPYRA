package net.study.springboot.model;

import lombok.*;
import net.study.springboot.domain.EmployeeHobbies;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeModel {

    private int id;
    private String firstName;
    private String lastName;
    private String emailId;
    private String city;
    private String state;
    private String country;
    private String phone;  // Changed from Integer to String
    private LocalDate dob;
    private LocalDate dateOfJoining;
    private BigDecimal salary;
    private int rating;
    private List<EmployeeHobbies> employeeHobbies;

}
