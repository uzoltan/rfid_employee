package ai.aitia.rfid_employee;

import ai.aitia.rfid_employee.exception.DuplicateEntryException;
import ai.aitia.rfid_employee.exception.ResourceNotFoundException;
import java.time.ZonedDateTime;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {

  private final EmployeeRepository employeeRepository;

  @Autowired
  public EmployeeController(EmployeeRepository repository) {
    employeeRepository = repository;
  }

  @GetMapping("employees")
  public Page<Employee> getEmployees(Pageable pageable) {
    return employeeRepository.findAll(pageable);
  }

  @GetMapping("employees/{tagId}")
  public Employee getEmployeeByRfidTagId(@PathVariable(value = "tagId") String tagId) {
    return employeeRepository.findByTagId(tagId).orElseThrow(() -> new ResourceNotFoundException("Employee with RFID tagID " + tagId + " not found"));
  }

  @PostMapping("employees")
  @ResponseStatus(HttpStatus.CREATED)
  public Employee addEmployee(@Valid @RequestBody Employee employee) {
    try {
      return employeeRepository.save(employee);
    } catch (DataIntegrityViolationException e) {
      e.printStackTrace();
      Optional<Employee> existingEmployee = employeeRepository.findByTagId(employee.getTagId());
      if (!existingEmployee.isPresent()) {
        //The exception root cause was not due to a unique ID violation then
        throw e;
      }
      throw new DuplicateEntryException(
          "An employee named " + existingEmployee.get().getName() + " already uses RFID tagID " + existingEmployee.get().getTagId());
    }
  }

  @PutMapping("employees/{tagId}")
  public Employee updateEmployee(@PathVariable(value = "tagId") String tagId) {
    return employeeRepository.findByTagId(tagId).map(employee -> {
      employee.setInside(!employee.isInside());
      employee.setLastSwipe(ZonedDateTime.now());
      return employeeRepository.save(employee);
    }).orElseThrow(() -> new ResourceNotFoundException("Employee with RFID tagID " + tagId + " not found"));
  }

  @DeleteMapping("employees/{tagId}")
  public ResponseEntity<?> deleteEmployee(@PathVariable(value = "tagId") String tagId) {
    return employeeRepository.findByTagId(tagId).map(employee -> {
      employeeRepository.delete(employee);
      return ResponseEntity.ok().build();
    }).orElseThrow(() -> new ResourceNotFoundException("Employee with RFID tagID " + tagId + " not found"));
  }

}
