package net.study.springboot.domain;

import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.isAlphabetic;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name="employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "empid")
    private int id;

    @NotNull
    @Column(name = "empfirstname")
    private String firstName;

    @NotNull
    @Column(name = "emplastname")
    private String lastName;

    @NotNull
    @Column(name = "empemail")
    private String emailId;

    @NotNull
    @Column(name = "empcity")
    private String city;

    @NotNull
    @Column(name = "empphone")
    private String phone; // corrected from Integer to String

    @Column(name = "empis_active")
    private boolean is_active;

    @Column(name = "empis_deleted")
    private boolean is_deleted;

    // ✅ Newly Added Fields

    @NotNull
    @Column(name = "empstate")
    private String state;

    @NotNull
    @Column(name = "empcountry")
    private String country;

    @NotNull
    @Column(name = "empdob")
    private LocalDate dob;

    @NotNull
    @Column(name = "empdate_join")
    private LocalDate dateOfJoining;

    @NotNull
    @Column(name = "empsalary")
    private BigDecimal salary;


    @Column(name = "emprating")
    private int rating;

    //other data fields you might want to add: DOB, start/end date, title or role, hourly wage
    @OneToMany(targetEntity = EmployeeHobbies.class, cascade = CascadeType.ALL)
    @JoinColumn(name="empid",referencedColumnName = "empid")
    private List<EmployeeHobbies> employeeHobbies;



}