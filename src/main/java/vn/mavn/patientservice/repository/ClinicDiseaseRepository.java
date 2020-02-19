package vn.mavn.patientservice.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.mavn.patientservice.entity.ClinicDisease;

public interface ClinicDiseaseRepository extends JpaRepository<ClinicDisease, Long> {

  List<ClinicDisease> findByDiseaseId(Long diseaseId);

  @Modifying
  @Query("delete from ClinicDisease cd where cd.clinicId = :clinicId")
  void deleteAllByClinicId(Long clinicId);

  @Query("select cd.diseaseId from ClinicDisease cd where cd.clinicId = :clinicId")
  List<Long> findAllDiseaseById(Long clinicId);
}
