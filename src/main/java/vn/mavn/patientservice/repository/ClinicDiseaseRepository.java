package vn.mavn.patientservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.mavn.patientservice.entity.ClinicDisease;

public interface ClinicDiseaseRepository extends JpaRepository<ClinicDisease, Long> {

}
