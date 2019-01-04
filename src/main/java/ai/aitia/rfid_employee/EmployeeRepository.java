package ai.aitia.rfid_employee;

import ai.aitia.rfid_employee.entity.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

  Optional<Employee> findByTagId(String tagId);

}
