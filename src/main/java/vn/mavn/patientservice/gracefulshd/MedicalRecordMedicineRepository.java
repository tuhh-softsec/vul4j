package vn.mavn.patientservice.gracefulshd;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import vn.mavn.patientservice.entity.MedicalRecordMedicine;

public interface MedicalRecordMedicineRepository extends
    CrudRepository<MedicalRecordMedicine, Long> {

  List<MedicalRecordMedicine> findAllByMedicineId(Long medicineId);

}
