package ai.aitia.rfid_employee.repository;

import ai.aitia.rfid_employee.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

}
