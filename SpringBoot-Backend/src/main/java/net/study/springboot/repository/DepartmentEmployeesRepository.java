package net.study.springboot.repository;

import net.study.springboot.domain.Department;
import net.study.springboot.domain.DepartmentEmployees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DepartmentEmployeesRepository extends JpaRepository<DepartmentEmployees, Integer> {

    @Query("SELECT COUNT(de) > 0 FROM DepartmentEmployees de WHERE de.departmentId = :departmentId AND de.employeeId = :employeeId")
    boolean existsEmployeeInDepartment(@Param("departmentId") int departmentId, @Param("employeeId") int employeeId);

    @Modifying
    @Transactional
    @Query("DELETE FROM DepartmentEmployees de WHERE de.departmentId = :departmentId AND de.employeeId = :employeeId")
    void deleteByDepartmentIdAndEmployeeId(@Param("departmentId") int departmentId, @Param("employeeId") int employeeId);

    @Transactional
    @Modifying
    @Query("DELETE FROM DepartmentEmployees eh WHERE eh.employeeId = :employeeId")
    void deleteByEmployeeId(@Param("employeeId") int employeeId);


}
