package net.study.springboot.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="hobbies")
public class Hobby {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hobbyid")
    private int id;
    @NotNull
    @Column(name = "hobbyname")
    private String name;
    @NotNull
    @Column(name = "hobbydesc")
    private String description;
//    @OneToMany(targetEntity = EmployeeHobbyMap.class, cascade = CascadeType.ALL)
//    @JoinColumn(name="hobbyid",referencedColumnName = "hobbyid")
//    List<Employee> employeeList;
}
