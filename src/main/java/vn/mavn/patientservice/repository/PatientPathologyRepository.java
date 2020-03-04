package vn.mavn.patientservice.repository;

import org.springframework.data.repository.CrudRepository;
import vn.mavn.patientservice.entity.PatientPathology;

public interface PatientPathologyRepository extends CrudRepository<PatientPathology, Long> {

}
