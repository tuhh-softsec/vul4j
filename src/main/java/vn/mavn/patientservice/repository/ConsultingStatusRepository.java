package vn.mavn.patientservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.mavn.patientservice.entity.ConsultingStatus;

public interface ConsultingStatusRepository extends JpaRepository<ConsultingStatus, Long> {

  @Query("select ads from ConsultingStatus ads where ads.code =:code")
  Optional<ConsultingStatus> findByCode(String code);

  @Query("select ads from ConsultingStatus ads where ads.code =:code")
  ConsultingStatus findActiveByCode(String code);
}
