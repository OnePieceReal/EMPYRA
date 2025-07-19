package net.study.springboot.controller;

import lombok.NoArgsConstructor;
import net.study.springboot.model.DepartmentModel;
import net.study.springboot.model.EmployeeModel;
import net.study.springboot.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.zip.DataFormatException;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/department")
@NoArgsConstructor
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    //Create a new department
    @PostMapping
    public ResponseEntity<DepartmentModel> createDepartment(@RequestBody DepartmentModel departmentModel)
            throws DataFormatException {
        DepartmentModel savedDepartment = departmentService.addDepartmentService(departmentModel);
        return new ResponseEntity<>(savedDepartment, HttpStatus.CREATED);
    }
    //Get all department + employee that work under them
    @GetMapping("/allDepartment")
    public ResponseEntity<List<DepartmentModel>> getAllDepartments() {
        List<DepartmentModel> departments = departmentService.getAllDepartmentService();
        return ResponseEntity.ok(departments);
    }

    //Update department
    @PutMapping("{id}")
        public ResponseEntity<DepartmentModel> updateDepartment(@PathVariable int id, @RequestBody DepartmentModel departmentModel) throws Exception  {
        return new ResponseEntity<DepartmentModel>(departmentService.updateDepartmentService(id,departmentModel),HttpStatus.ACCEPTED);
    }
    //Delete a department
    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> deleteEmployee(@PathVariable int id) {
        departmentService.deleteDepartmentService(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Add an employee to a department
    @PostMapping("/{depid}/employees/{empid}")
    public ResponseEntity<HttpStatus> addEmployeeToDepartment(@PathVariable int depid, @PathVariable int empid) {
        departmentService.addEmployeeToDepartmentService(depid, empid);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // Remove an employee from a department
    @DeleteMapping("/{depid}/employees/{empid}")
    public ResponseEntity<HttpStatus> removeEmployeeFromDepartment(@PathVariable int depid, @PathVariable int empid) {
        departmentService.removeEmployeeFromDepartmentService(depid, empid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
