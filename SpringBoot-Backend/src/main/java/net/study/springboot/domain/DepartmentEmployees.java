package net.study.springboot.domain;

import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "department_employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentEmployees {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="dep_emp_id")
    private int depEmpId;

    @Column(name="depid")
    private int departmentId;

    @Column(name="empid")
    private int employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depid", insertable = false, updatable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empid", insertable = false, updatable = false)
    private Employee employee;

    public DepartmentEmployees(int departmentId, int employeeId) {
    this.departmentId = departmentId;
    this.employeeId = employeeId;
}
}
