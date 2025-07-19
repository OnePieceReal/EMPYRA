package net.study.springboot.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EmployeeSummary {
    private int id;
    private String firstName;
    private String lastName;
}
