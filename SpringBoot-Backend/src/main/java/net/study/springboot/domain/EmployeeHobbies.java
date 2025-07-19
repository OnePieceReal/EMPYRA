package net.study.springboot.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name="employee_hobbies")
public class EmployeeHobbies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_hob_id")
    private int id;
    //we use the statement "insertable=false, updatable=false" when we need to map a field more than once in an entity
    @Column(name = "empid")
    private int employeeId;
    @Column(name = "hobbyid")
    private int hobbyId;
    @OneToOne(targetEntity = Hobby.class, cascade = CascadeType.ALL)
    @JoinColumn(name= "hobbyid",referencedColumnName = "hobbyid",insertable=false, updatable=false)
    private Hobby hobby;
//    @OneToOne(targetEntity = Employee.class, cascade = CascadeType.ALL)
//    @JoinColumn(name= "empid",referencedColumnName = "empid")
//    Employee employee;
    public EmployeeHobbies(int employeeId, int hobbyId){
        this.employeeId=employeeId;
        this.hobbyId=hobbyId;
    }
}
