package net.study.springboot.repository;

import net.study.springboot.domain.Employee;
import net.study.springboot.domain.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HobbyRepository extends JpaRepository<Hobby, Integer> {
//    @Query(value = "SELECT a " +
//            "from Employee a JOIN EmployeeHobbies b on b.employeeId = a.id JOIN Hobby c on b.hobbyId=c.id " +
//            "WHERE c.id = :id " +
//            "order by a.id DESC")
//    public List<Employee> getAllEmployeesByHobbyId(@Param("id") int hobbyid);

    boolean existsByName(String name);

    @Query("SELECT h.id FROM Hobby h WHERE h.name = :name")
    Integer findIdByName(@Param("name") String name);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM Hobby h WHERE h.name = :name AND h.id <> :id")
    boolean existsByNameExcludingId(@Param("name") String name, @Param("id") int id);


}
