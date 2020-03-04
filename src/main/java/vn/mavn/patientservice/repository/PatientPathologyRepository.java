package vn.mavn.patientservice.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import vn.mavn.patientservice.entity.PatientPathology;

public interface PatientPathologyRepository extends CrudRepository<PatientPathology, Long> {

  @Query("select pp from PatientPathology pp where pp.patientId = :patientId")
  List<PatientPathology> findAllByPatientId(Long patientId);
}
