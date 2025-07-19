package net.study.springboot.model;

import lombok.Data;

@Data
public class EmployeeModelWithOutList {

    private int id;
    private String firstName;
    private String lastName;
    private String emailId;
    private String city;
    private Integer phone;
}
