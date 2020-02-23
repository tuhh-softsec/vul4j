package vn.mavn.patientservice.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import vn.mavn.patientservice.entity.MedicalRecordMedicine;

public interface MedicalRecordMedicineRepository extends
    CrudRepository<MedicalRecordMedicine, Long> {

  List<MedicalRecordMedicine> findAllByMedicineId(Long medicineId);

  @Query("select mm from MedicalRecordMedicine mm where mm.medicalRecordId =:medicalRecordId")
  List<MedicalRecordMedicine> findAllByMedicalRecordId(Long medicalRecordId);
}
