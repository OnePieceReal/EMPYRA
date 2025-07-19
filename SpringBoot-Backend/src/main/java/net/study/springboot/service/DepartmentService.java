package net.study.springboot.service;

import lombok.NoArgsConstructor;
import net.study.springboot.domain.Department;
import net.study.springboot.domain.DepartmentEmployees;
import net.study.springboot.domain.Employee;
import net.study.springboot.exception.DataFormatException;
import net.study.springboot.exception.ResourceAlreadyExistException;
import net.study.springboot.exception.ResourceNotFoundException;
import net.study.springboot.helper.DepartmentModelConverter;
import net.study.springboot.helper.EmployeeModelConverter;
import net.study.springboot.model.DepartmentModel;
import net.study.springboot.model.EmployeeModel;
import net.study.springboot.repository.DepartmentEmployeesRepository;
import net.study.springboot.repository.DepartmentRepository;
import net.study.springboot.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private DepartmentEmployeesRepository departmentEmployeesRepository;
    @Autowired
    private DepartmentModelConverter departmentModelConverter;
    @Autowired
    private EmployeeRepository employeeRepository;
    public boolean validateAllParameters(DepartmentModel departmentModel){
        if(departmentModel.getDescription()==null || departmentModel.getName()==null||
        departmentModel.getDescription().length()==0 || departmentModel.getName().length()==0
        || departmentModel.getEmployees()==null){
            return false;
        }
        return true;
    }
    //Add a new department
    public DepartmentModel addDepartmentService(DepartmentModel departmentModel) {
        Department department = departmentModelConverter.convertDepartmentModelModeltoDepartment(departmentModel);
        //check if resource already exists
        if(departmentRepository.existsByName(department.getName()) || departmentRepository.existsById(department.getId())){
            throw new ResourceAlreadyExistException("Department already exists!");
        }
        //validate parameters
        if(!validateAllParameters(departmentModel)){
            throw new DataFormatException("Invalid data format");
        }
        departmentRepository.save(department);
        departmentModel.setId(departmentRepository.findByName(departmentModel.getName()).getId());
        return departmentModel;
    }

    //List all departments
    public List<DepartmentModel> getAllDepartmentService() {
        List<DepartmentModel> departmentModels = departmentRepository.getAllDepartments().stream()
                .map(departmentModelConverter::convertDepartmenttoDepartmentModel)
                .collect(Collectors.toList());
        return departmentModels;
    }

    //Update a department
    public DepartmentModel updateDepartmentService(int id,DepartmentModel departmentModel){
        //check if the provided parameters are valid
        if(!validateAllParameters(departmentModel) || id != departmentModel.getId()){
            throw new DataFormatException("Invalid data format");
        }
        //check if the provided id even exists
        if(!departmentRepository.existsById(departmentModel.getId())){
            throw new ResourceNotFoundException("Resource does not exist");
        }
        // if the new department name already exist in the dataset
        if(departmentRepository.existsByNameExcludingId(departmentModel.getName(),departmentModel.getId())){
            throw new ResourceAlreadyExistException("Data already exist");
        }

        Department department = departmentModelConverter.convertDepartmentModelModeltoDepartment(departmentModel);
        departmentRepository.save(department);
        return departmentModel;
    }

    //Delete a department
    //using cascade delete - so all mention of department id will be deleted
    public void deleteDepartmentService(int id){
        if(!departmentRepository.existsById(id)){
          throw new ResourceNotFoundException("Resource not found - deletion - department ");
        }
        departmentRepository.deleteById(id);
    }

    // Add an employee to the department
    public void addEmployeeToDepartmentService(int departmentId, int employeeId) {
        // Check if employee and department exist
        if (!employeeRepository.existsById(employeeId) || !departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Resource not found - employee or department does not exist.");
        }

        // Check if the employee is already added to the department
        boolean alreadyExists = departmentEmployeesRepository.existsEmployeeInDepartment(departmentId, employeeId);
        if (alreadyExists) {
            throw new ResourceAlreadyExistException("Employee is already assigned to the department.");
        }

        // Save the relationship
        DepartmentEmployees departmentEmployees = new DepartmentEmployees(departmentId, employeeId);
        departmentEmployeesRepository.save(departmentEmployees);
    }

    //Delete employee from department
    public void removeEmployeeFromDepartmentService(int departmentId, int employeeId) {
        // Check if employee and department exist
        if (!employeeRepository.existsById(employeeId) || !departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Resource not found - employee or department does not exist.");
        }

        // Check if the employee is actually assigned to the department
        boolean exists = departmentEmployeesRepository.existsEmployeeInDepartment(departmentId, employeeId);
        if (!exists) {
            throw new ResourceNotFoundException("Employee is not assigned to the department.");
        }

        // Perform the delete
        departmentEmployeesRepository.deleteByDepartmentIdAndEmployeeId(departmentId, employeeId);
    }



}
