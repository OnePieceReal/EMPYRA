package net.study.springboot.repository;
import net.study.springboot.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    //@Query(value = "SELECT * FROM EMPLOYEES ", nativeQuery = true)
    //public List<Employee> getAllEmployeeList();
    //@Query(value = "SELECT * FROM EMPLOYEES WHERE empemail like CONCAT('%',:eval,'%');", nativeQuery = true)
    //public List<Employee> findEmployeeByEmail(@Param("eval") String email);
    //query using jpql
    @Query(value = "SELECT e FROM Employee  e WHERE e.is_deleted=FALSE")
    public LinkedList<Employee> getActiveEmployeeList();
    @Query(value = "SELECT e FROM Employee  e ")
    public LinkedList<Employee> getAllEmployeeList();

    // Check if an email already exists, excluding a specific employee ID
    boolean existsByEmailIdAndIdNot(String emailId, int id);

    boolean existsByEmailId(String emailId);


}
