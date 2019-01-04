package ai.aitia.rfid_employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "ai.aitia.rfid_employee.repository")
public class RfidEmployeeApplication {

  public static void main(String[] args) {
    SpringApplication.run(RfidEmployeeApplication.class, args);
  }

}

