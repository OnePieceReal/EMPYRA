package net.study.springboot.helper;

import net.study.springboot.domain.Employee;
import net.study.springboot.model.EmployeeModel;
import net.study.springboot.model.EmployeeModelWithOutList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeModelConverter {
    @Autowired
    private ModelMapper modelMapper;
    //EmployeeModel
    public EmployeeModel convertEmployeeToEmployeeModel(Employee employee){
        EmployeeModel employeeModel =modelMapper.map(employee, EmployeeModel.class);
        return employeeModel;
    }
    public Employee convertEmployeeModelToEmployee(EmployeeModel employeeModel){
        Employee employee = modelMapper.map(employeeModel,Employee.class);
        return employee;
    }
    //Employee Model Without List
    public EmployeeModelWithOutList convertEmployeeToEmployeeModelWOList(Employee employee){
        EmployeeModelWithOutList employeeModelWithOutList = modelMapper.map(employee, EmployeeModelWithOutList.class);
        return employeeModelWithOutList;
    }



}
