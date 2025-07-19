package net.study.springboot.repository;

import net.study.springboot.domain.Department;
import net.study.springboot.domain.EmployeeHobbies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EmployeeHobbiesRepository extends JpaRepository<EmployeeHobbies, Integer> {
    @Query("SELECT COUNT(eh) > 0 FROM EmployeeHobbies eh WHERE eh.employeeId = :employeeId AND eh.hobbyId = :hobbyId")
    boolean existsByEmployeeIdAndHobbyId(@Param("employeeId") int employeeId, @Param("hobbyId") int hobbyId);

    @Transactional
    @Modifying
    @Query("DELETE FROM EmployeeHobbies eh WHERE eh.employeeId = :employeeId AND eh.hobbyId = :hobbyId")
    void deleteByEmployeeIdAndHobbyId(@Param("employeeId") int employeeId, @Param("hobbyId") int hobbyId);
    @Transactional
    @Modifying
    @Query("DELETE FROM EmployeeHobbies eh WHERE eh.employeeId = :employeeId")
    void deleteByEmployeeId(@Param("employeeId") int employeeId);
}
