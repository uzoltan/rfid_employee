package ai.aitia.rfid_employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZonedDateTime;
import java.util.List;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = RfidEmployeeApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TestPropertySource(locations = "classpath:application-test.properties")
public class EmployeeControllerIntegrationTests {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private EmployeeRepository repository;

  // @formatter:off
  @Test
  public void _01addEmployee_whenValidInput_thenCreateEmployee() throws Exception {
    Employee sampleEmployee = new Employee("tagId01", "John Doe", false, ZonedDateTime.now());
    mvc.perform(post("/employees").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(sampleEmployee)))
       .andExpect(status().isCreated())
       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
       .andExpect(jsonPath("$.tagId").value("tagId01"))
       .andExpect(jsonPath("$.name").value("John Doe"))
       .andExpect(jsonPath("$.inside").value(false));
    //lastSwipe ZonedDateTime value might be formatted differently when returned by the repository.save() method

    List<Employee> employees = repository.findAll();
    assertThat(employees).extracting(Employee::getName).containsOnly("John Doe");
  }

  //@Test
  public void _02addEmployee_whenDuplicateTagId_thenExceptionIsReturned() throws Exception {
    Employee sampleEmployee = new Employee("tagId01", "John Doe");
    mvc.perform(post("/employees").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(sampleEmployee)))
       .andExpect(status().isConflict())
       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
       .andExpect(jsonPath("$.message").value("An employee named John Doe already uses RFID tagID tagId01"));


    int employeeCount = repository.findAll().size();
    Assert.assertEquals(1, employeeCount);
  }

  @Test
  public void _03getEmployees_returnsTheCorrectNumberOfEmployees() throws Exception {
    mvc.perform(get("/employees"))
       .andExpect(status().isOk())
       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
       .andExpect(jsonPath("$.totalElements").value(1));

    Employee tempEmployee = new Employee("tagId02", "Jane Doe");
    repository.save(tempEmployee);
    mvc.perform(get("/employees"))
       .andExpect(status().isOk())
       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
       .andExpect(jsonPath("$.totalElements").value(2));

    repository.delete(tempEmployee);
    mvc.perform(get("/employees"))
       .andExpect(status().isOk())
       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
       .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  public void _04getEmployeeByRfidTagId_returnsTheCorrectEmployee() throws Exception {
    mvc.perform(get("/employees/tagId01"))
       .andExpect(status().isOk())
       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
       .andExpect(jsonPath("$.tagId").value("tagId01"))
       .andExpect(jsonPath("$.name").value("John Doe"))
       .andExpect(jsonPath("$.inside").value(false));
  }

  @Test
  public void _05updateEmployee_whenTagIdExists_thenUpdateEmployeeStatus() throws Exception{
    mvc.perform(put("/employees/tagId01"))
       .andExpect(status().isOk())
       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
       .andExpect(jsonPath("$.tagId").value("tagId01"))
       .andExpect(jsonPath("$.name").value("John Doe"))
       .andExpect(jsonPath("$.inside").value(true))
       .andExpect(jsonPath("$.lastSwipe").isString());
  }

  //@Test
  public void _06updateEmployee_whenTagIdDoesNotExists_thenExceptionIsReturned() throws Exception{
    mvc.perform(put("/employees/tagId999"))
       .andExpect(status().isNotFound())
       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
       .andExpect(jsonPath("$.message").value("Employee with RFID tagID tagId999 not found"));
  }

  @Test
  public void _07deleteEmployee_whenTagIdExists_thenDeleteEmployee() throws Exception {
    mvc.perform(delete("/employees/tagId01"))
       .andExpect(status().isOk());
  }

  //@Test
  public void _08deleteEmployee_whenTagIdDoesNotExists_thenExceptionIsReturned() throws Exception {
    mvc.perform(delete("/employees/tagId999"))
       .andExpect(status().isNotFound())
       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
       .andExpect(jsonPath("$.message").value("Employee with RFID tagID tagId999 not found"));
  }
  // @formatter:on
}