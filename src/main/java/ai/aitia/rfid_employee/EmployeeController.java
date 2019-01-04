package ai.aitia.rfid_employee;

import ai.aitia.rfid_employee.entity.Employee;
import ai.aitia.rfid_employee.entity.History;
import ai.aitia.rfid_employee.exception.DuplicateEntryException;
import ai.aitia.rfid_employee.exception.ResourceNotFoundException;
import ai.aitia.rfid_employee.repository.EmployeeRepository;
import ai.aitia.rfid_employee.repository.HistoryRepository;
import java.time.ZonedDateTime;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
  private final HistoryRepository historyRepository;

  @Autowired
  public EmployeeController(EmployeeRepository employeeRepo, HistoryRepository historyRepo) {
    employeeRepository = employeeRepo;
    historyRepository = historyRepo;
  }

  @GetMapping("employees")
  public Page<Employee> getEmployees(Pageable pageable) {
    return employeeRepository.findAll(pageable);
  }

  @GetMapping("employees/{tagId}")
  public Employee getEmployeeByRfidTagId(@PathVariable(value = "tagId") String tagId) {
    return employeeRepository.findByTagId(tagId).orElseThrow(() -> new ResourceNotFoundException("Employee with RFID tagID " + tagId + " not found"));
  }

  @GetMapping(value = "employees/in", produces = MediaType.TEXT_HTML_VALUE)
  public String getNumberOfEmployeesAtWork() {
    int count = Math.toIntExact(employeeRepository.countByInside(true));

    // I'm not liable for this "master piece"
    // @formatter:off
    String htmlHead = "<!DOCTYPE html>\n"
                    + "<meta http-equiv=\"Refresh\" content=\"5\">\n"
                    + "<html>\n"
                    + "<head>\n"
                    + "<title>AITIA Employee Counter</title>\n"
                    + "</head>\n"
                    + "<body>\n"
                    + "<h1 style=\"margin-top: 5em; text-align:center\">";

    String sentence;
    if (count == 0) {
      sentence = "We have like zero people inside yo. Business as usual.";
    } else if (count == 1) {
      sentence = "There is currently only 1 person inside.";
    } else {
      sentence = "There are currently " + count + " people inside.";
    }

    String htmlTail = "</h1>\n"
                    + "</body>\n"
                    + "</html>";

    return htmlHead + sentence + htmlTail;
    // @formatter:on
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
      employee = employeeRepository.save(employee);
      historyRepository.save(new History(employee));
      return employee;
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
