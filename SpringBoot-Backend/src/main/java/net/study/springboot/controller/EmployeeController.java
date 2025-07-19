package net.study.springboot.controller;
import lombok.NoArgsConstructor;
import net.study.springboot.domain.Employee;
import net.study.springboot.model.EmployeeModel;
import net.study.springboot.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.zip.DataFormatException;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/employees")
@NoArgsConstructor
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    //find all active employee RA
    @GetMapping
    public List<EmployeeModel> getAllActiveEmployee(){
        return employeeService.getAllActiveEmployeesService();
    }
    //find all employee RA
    @GetMapping("/allEmployee")
    public List<EmployeeModel> getAllEmployees(){
        return employeeService.getAllEmployeesService();
    }
    // find employee by ID RA
    @GetMapping("{id}")
    public ResponseEntity<EmployeeModel> getEmployeeId(@PathVariable int id){
        return new ResponseEntity<EmployeeModel>(employeeService.getEmployeeByIdService(id),HttpStatus.OK);
    }
    //Create Employee RA
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EmployeeModel> createEmployee (@RequestBody EmployeeModel employeeModel) throws DataFormatException {
        return new ResponseEntity<EmployeeModel>(employeeService.addEmployeeService(employeeModel),HttpStatus.CREATED);
    }
    //Update Employee RA
    @PutMapping("{id}")
    public ResponseEntity<EmployeeModel> updateEmployee(@PathVariable int id, @RequestBody EmployeeModel employeeModel) throws Exception{
        return new ResponseEntity<EmployeeModel>(employeeService.updateEmployeeService(id,employeeModel),HttpStatus.ACCEPTED);

    }
    //Delete Employee RA
    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> deleteEmployee(@PathVariable int id){
        employeeService.deleteEmployeeService(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Add a hobby to an employee
    @PostMapping("/{employeeId}/hobbies/{hobbyId}")
    public ResponseEntity<HttpStatus> addHobbyToEmployee(
            @PathVariable int employeeId,
            @PathVariable int hobbyId
    ) throws Exception {
        employeeService.addHobbyToEmployeeService(employeeId, hobbyId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    //  Remove a hobby from an employee
    @DeleteMapping("/{employeeId}/hobbies/{hobbyId}")
    public ResponseEntity<HttpStatus> removeHobbyFromEmployee(
            @PathVariable int employeeId,
            @PathVariable int hobbyId
    ) {
        employeeService.removeHobbyFromEmployeeService(employeeId, hobbyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}
