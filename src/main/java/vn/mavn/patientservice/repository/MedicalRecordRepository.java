package vn.mavn.patientservice.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.mavn.patientservice.entity.MedicalRecord;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long>,
    JpaSpecificationExecutor<MedicalRecord> {

  @Query("select mr from MedicalRecord mr where mr.advertisingSourceId =:avertId")
  List<MedicalRecord> findByAvertId(Long avertId);

  @Query("select mr from MedicalRecord mr where mr.patientId =:patientId")
  List<MedicalRecord> findByPatientId(Long patientId);

  @Query("select mr from MedicalRecord mr where mr.diseaseId = :diseaseId")
  List<MedicalRecord> findByDiseaseId(Long diseaseId);

  @Query("select mr from MedicalRecord mr where mr.id =:id and mr.isActive = true")
  Optional<MedicalRecord> findActiveById(Long id);
}
