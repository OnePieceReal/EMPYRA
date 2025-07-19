package net.study.springboot;
import net.study.springboot.helper.EmployeeModelConverter;
import net.study.springboot.repository.DepartmentEmployeesRepository;
import net.study.springboot.repository.DepartmentRepository;
import net.study.springboot.repository.EmployeeRepository;
import net.study.springboot.repository.HobbyRepository;
import net.study.springboot.service.DepartmentService;
import net.study.springboot.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootBackendApplication implements CommandLineRunner {

	public static void main(String[] args) {

		SpringApplication.run(SpringbootBackendApplication.class, args);

	}
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private HobbyRepository hobbyRepository;
	@Autowired
	private EmployeeService empservice;
	@Autowired
	private EmployeeModelConverter employeeModelConverter;
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private DepartmentEmployeesRepository departmentEmployeesRepository;
	@Override
	public void run(String... args) throws Exception {
//		List<Employee> list = hobbyRepository.getAllEmployeesByHobbyId(1);
//		for(Employee element : list){
//			System.out.println(element.getId());
//		}
//		Employee employee = new Employee(1,"a","b","gwwc@gmail.com","hellocity",1111111111,true,false,null);
//		EmployeeModel employeeModel=employeeModelConverter.convertEmployeeToEmployeeModel(employee);
//		empservice.addEmployeeService(employeeModel);



	}
}
