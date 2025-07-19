package net.study.springboot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.study.springboot.domain.Employee;
import net.study.springboot.helper.EmployeeSummary;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentModel {
    private int id;
    private String name;
    private String description;
    public List<EmployeeSummary> employees;
}
