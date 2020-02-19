package vn.mavn.patientservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.mavn.patientservice.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

}
