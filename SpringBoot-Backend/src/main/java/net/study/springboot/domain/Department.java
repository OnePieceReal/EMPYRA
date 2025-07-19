package net.study.springboot.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;
import net.study.springboot.helper.EmployeeSummary;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "depid")
    private int id;
    @NotNull
    @Column(name = "depname")
    private String name;
    @NotNull
    @Column(name = "depdesc")
    private String description;

    @ManyToMany
    @JoinTable(
            name = "department_employees",
            joinColumns = @JoinColumn(name = "depid"),
            inverseJoinColumns = @JoinColumn(name = "empid")
    )
    private List<Employee> employees;

    // Get a list of employee summaries (id + full name)
    @Transient
    public List<EmployeeSummary> getEmployeeSummaries() {
        return employees.stream()
                .map(emp -> new EmployeeSummary(
                        emp.getId(),
                        emp.getFirstName(),
                        emp.getLastName()
                ))
                .collect(Collectors.toList());
    }
}
