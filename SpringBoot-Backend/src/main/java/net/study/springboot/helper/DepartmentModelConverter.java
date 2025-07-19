package net.study.springboot.helper;

import net.study.springboot.domain.Department;
import net.study.springboot.domain.Hobby;
import net.study.springboot.model.DepartmentModel;
import net.study.springboot.model.HobbyModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DepartmentModelConverter {
    @Autowired
    private ModelMapper modelMapper;
    //conversion for the hobby class
    public DepartmentModel convertDepartmenttoDepartmentModel(Department department){
        DepartmentModel departmentModel = new DepartmentModel(department.getId(),department.getName(),department.getDescription(),department.getEmployeeSummaries());
        return departmentModel;
    }
        public Department convertDepartmentModelModeltoDepartment(DepartmentModel departmentModel){
        Department department=modelMapper.map(departmentModel,Department.class);
        return department;
    }

}
