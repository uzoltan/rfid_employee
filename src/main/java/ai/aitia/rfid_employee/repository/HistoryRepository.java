package ai.aitia.rfid_employee.repository;

import ai.aitia.rfid_employee.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {

}
