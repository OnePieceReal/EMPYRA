package net.study.springboot.service;

import lombok.NoArgsConstructor;
import net.study.springboot.domain.EmployeeHobbies;
import net.study.springboot.model.EmployeeModel;
import net.study.springboot.helper.EmployeeModelConverter;
import net.study.springboot.exception.ResourceAlreadyExistException;
import net.study.springboot.exception.ResourceNotFoundException;
import net.study.springboot.domain.Employee;
import net.study.springboot.model.HobbyModel;
import net.study.springboot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import static java.lang.Character.isAlphabetic;

@NoArgsConstructor
@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeModelConverter employeeModelConverter;
    @Autowired
    private EmployeeHobbiesRepository employeeHobbiesRepository;
    @Autowired
    private HobbyRepository hobbyRepository;
    @Autowired
    private DepartmentEmployeesRepository departmentEmployeesRepository;
    // Check if a given name is valid
    public boolean isValidName(String name) {
        if (name == null || name.length() == 0 || name.length()>255) {
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            if (!isAlphabetic(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    //email validation
    //RFC822 compliant regex adapted for Java:
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public boolean isValidEmail(String emailId) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailId);
        return matcher.matches();
    }
    public boolean isValidPhoneNumber(String phone) {
        if (phone == null) {
            return false;
        }
        // Check if it only contains digits and is 10 to 15 digits long
        return phone.matches("^\\d{10,15}$");
    }

    public boolean isValidDateOfBirth(LocalDate dob) {
        if (dob == null) return false;
        LocalDate today = LocalDate.now();
        // Example rule: dob must be at least 18 years ago and not in future
        return !dob.isAfter(today) && dob.isBefore(today.minusYears(18));
    }

    public boolean isValidDateOfJoining(LocalDate doj, LocalDate dob) {
        if (doj == null || dob == null) return false;
        LocalDate today = LocalDate.now();
        // Date of joining can't be in future and after dob + 18 years
        return !doj.isAfter(today) && !doj.isBefore(dob.plusYears(18));
    }

    public boolean isValidCityOrCountry(String value) {
        if (value == null || value.trim().isEmpty() || value.length() > 255) return false;
        // Allow alphabetic and spaces only (e.g., "New York")
        return value.matches("^[A-Za-z ]+$");
    }
    public boolean isValidSalary(BigDecimal salary) {
        return salary != null && salary.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 10;
    }


    //validate all parameters
    public boolean validateAllParameters(EmployeeModel employeeModel) {
        return isValidName(employeeModel.getFirstName()) &&
                isValidName(employeeModel.getLastName()) &&
                isValidEmail(employeeModel.getEmailId()) &&
                isValidPhoneNumber(employeeModel.getPhone()) &&
                isValidDateOfBirth(employeeModel.getDob()) &&
                isValidDateOfJoining(employeeModel.getDateOfJoining(), employeeModel.getDob()) &&
                isValidCityOrCountry(employeeModel.getCity()) &&
                isValidCityOrCountry(employeeModel.getCountry()) &&
                isValidSalary(employeeModel.getSalary()) &&
                isValidRating(employeeModel.getRating());
    }


    //add employee service
    //add user :  is_active = true and is_deleted = false
    public EmployeeModel addEmployeeService(EmployeeModel employeeModel) throws DataFormatException {
        Employee employee=employeeModelConverter.convertEmployeeModelToEmployee(employeeModel);
        if(employeeRepository.findById(employee.getId()).isPresent() || employeeRepository.existsByEmailId(employeeModel.getEmailId())){
            throw new ResourceAlreadyExistException(employee.getId()+" already exist");
        }
        if(validateAllParameters(employeeModel)){
            employee.set_active(true);
            employee.set_deleted(false);
            employeeRepository.save(employee);
        }
        else {
            throw new DataFormatException("Invalid Data Format");
        }
        return employeeModelConverter.convertEmployeeToEmployeeModel(employee);
    }
    //update employee service
    public EmployeeModel updateEmployeeService(int id, EmployeeModel employeeModel) throws Exception{
        // Validate parameters and ID consistency
        if (!validateAllParameters(employeeModel) || id != employeeModel.getId()) {
            throw new DataFormatException("Invalid data format");
        }

        // Check if employee exists
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee does not exist with id: " + id);
        }

        // (Optional) Add duplicate check logic if needed
        // Example: check for duplicate email excluding current ID
        // if (employeeRepository.existsByEmailExcludingId(employeeModel.getEmailId(), id)) {
        //     throw new ResourceAlreadyExistException("Email already in use by another employee");
        // }

        // Convert model to entity using ModelMapper
        Employee employee = employeeModelConverter.convertEmployeeModelToEmployee(employeeModel);

        // Save and return converted model
        employee.set_deleted(false);
        employee.set_active(true);
        Employee saved = employeeRepository.save(employee);
        return employeeModelConverter.convertEmployeeToEmployeeModel(saved);
    }

    //delete employee service
    //delete operation -> the is_deleted column should set as boolean true
    public void deleteEmployeeService(int id){
        Employee employee = employeeRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("employee does not exist with id:" + id));
        employee.set_deleted(true);
        employeeRepository.save(employee);
        // Hard delete related entries from employee_hobbies table
        employeeHobbiesRepository.deleteByEmployeeId(id);
        departmentEmployeesRepository.deleteByEmployeeId(id);
    }
    //get all active employee service
    public List<EmployeeModel> getAllActiveEmployeesService(){
        List<Employee> employeesList = employeeRepository.getActiveEmployeeList();
        List<EmployeeModel> employeeModelsList=new ArrayList<>();
        for(Employee element: employeesList){
            employeeModelsList.add(employeeModelConverter.convertEmployeeToEmployeeModel(element));
        }
        return employeeModelsList;
    }

    // get all employee service;
    public EmployeeModel getEmployeeByIdService(int id){
        return employeeModelConverter.convertEmployeeToEmployeeModel(employeeRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("employee does not exist with id:" + id)));
    }
    //get both deleted and active employee
    public List<EmployeeModel> getAllEmployeesService(){
        List<Employee> employeesList = employeeRepository.getAllEmployeeList();
        List<EmployeeModel> employeeModelsList=new ArrayList<>();
        for(Employee element: employeesList){
            employeeModelsList.add(employeeModelConverter.convertEmployeeToEmployeeModel(element));
        }
        return employeeModelsList;
    }

    //Add hobbies to employee
    public void addHobbyToEmployeeService(int employeeId, int hobbyId) throws Exception {

        // Fetch employee and check soft-delete
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (employee.is_deleted()) {
            throw new ResourceNotFoundException("Employee is deleted");
        }
        // Check if hobby exists
        if (!hobbyRepository.existsById(hobbyId)) {
            throw new ResourceNotFoundException("Hobby not found");
        }
        // Check for duplicate relationship
        if (employeeHobbiesRepository.existsByEmployeeIdAndHobbyId(employeeId, hobbyId)) {
            throw new ResourceAlreadyExistException("This hobby is already assigned to the employee");
        }
        // Save the relationship
        EmployeeHobbies employeeHobbies = new EmployeeHobbies();
        employeeHobbies.setEmployeeId(employeeId);
        employeeHobbies.setHobbyId(hobbyId);
        employeeHobbiesRepository.save(employeeHobbies);
    }

    public void removeHobbyFromEmployeeService(int employeeId, int hobbyId) {
        // Check if the relationship exists
        if (!employeeHobbiesRepository.existsByEmployeeIdAndHobbyId(employeeId, hobbyId)) {
            throw new ResourceNotFoundException("Hobby not assigned to employee");
        }

        // Delete the relationship
        employeeHobbiesRepository.deleteByEmployeeIdAndHobbyId(employeeId, hobbyId);
    }




}
