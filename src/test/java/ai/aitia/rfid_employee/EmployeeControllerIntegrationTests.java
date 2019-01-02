package ai.aitia.rfid_employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZonedDateTime;
import java.util.List;
import org.junit.After;
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
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class EmployeeControllerIntegrationTests {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private EmployeeRepository repository;

  @After
  public void resetDb() {
    repository.deleteAll();
  }

  @Test
  public void _01whenValidInput_thenCreateEmployee() throws Exception {
    Employee sampleEmployee = new Employee("tagId01", "Varga Pál", false, ZonedDateTime.now());
    mvc.perform(post("/employees").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(sampleEmployee))).andExpect(status().isCreated());

    List<Employee> employees = repository.findAll();
    assertThat(employees).extracting(Employee::getName).containsOnly("Varga Pál");

    /*// @formatter:off
    mvc.perform(get("/api/employees").contentType(MediaType.APPLICATION_JSON))
       .andDo(print())
       .andExpect(status().isOk())
       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
       .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
       .andExpect(jsonPath("$[0].name", is("bob")))
       .andExpect(jsonPath("$[1].name", is("alex")));
    // @formatter:on*/
  }

}