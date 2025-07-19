package net.study.springboot.repository;

import net.study.springboot.domain.Department;
import net.study.springboot.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public interface DepartmentRepository  extends JpaRepository<Department, Integer> {

    //  Read all departments (JPQL)
    @Query("SELECT d FROM Department d")
    LinkedList<Department> getAllDepartments();


    @Query("SELECT d FROM Department d WHERE d.name = :name")
    Department findByName(@Param("name") String name);

    //  Get employees in a department by depid
    @Query("SELECT e FROM Department d JOIN d.employees e WHERE d.id = :id")
    List<Employee> getEmployeesByDepartmentId(@Param("id") int depid);

    //  Delete department by ID (optional custom form)
    @Modifying
    @Query("DELETE FROM Department d WHERE d.id = :id")
    void deleteByIdCustom(@Param("id") int id);

    //  Check existence by name (for uniqueness)
    boolean existsByName(String name);

    @Query("SELECT COUNT(d) > 0 FROM Department d WHERE d.name = :name AND d.id <> :id")
    boolean existsByNameExcludingId(@Param("name") String name, @Param("id") int id);


}
