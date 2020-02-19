package vn.mavn.patientservice.service.impl;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.mavn.patientservice.dto.MedicineAddDto;
import vn.mavn.patientservice.dto.MedicineEditDto;
import vn.mavn.patientservice.dto.qobject.QueryMedicineDto;
import vn.mavn.patientservice.entity.MedicalRecordMedicine;
import vn.mavn.patientservice.entity.Medicine;
import vn.mavn.patientservice.exception.ConflictException;
import vn.mavn.patientservice.exception.NotFoundException;
import vn.mavn.patientservice.gracefulshd.MedicalRecordMedicineRepository;
import vn.mavn.patientservice.repository.DiseaseRepository;
import vn.mavn.patientservice.repository.MedicineRepository;
import vn.mavn.patientservice.repository.spec.MedicineSpec;
import vn.mavn.patientservice.service.MedicineService;

@Service
public class MedicineServiceImpl implements MedicineService {

  @Autowired
  private MedicineRepository medicineRepository;

  @Autowired
  private DiseaseRepository diseaseRepository;

  @Autowired
  private MedicalRecordMedicineRepository medicalRecordMedicineRepository;

  @Override
  public Page<Medicine> getAllMedicines(QueryMedicineDto data, Pageable pageable) {
    return medicineRepository.findAll(MedicineSpec.findAllMedicines(data), pageable);
  }

  @Override
  public Medicine add(MedicineAddDto data) {
    medicineRepository.findByName(data.getName().trim()).ifPresent(medicine -> {
      throw new ConflictException(
          Collections.singletonList("err.medicines.medicine-already-exists"));
    });
    diseaseRepository.findById(data.getDiseaseId())
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err.diseases.disease-not-found")));
    Medicine medicine = new Medicine();
    BeanUtils.copyProperties(data, medicine);
    medicineRepository.save(medicine);
    return medicine;
  }

  @Override
  public Medicine update(MedicineEditDto data) {
    Medicine medicine = medicineRepository.findById(data.getId())
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err.medicines.medicine-not-found")));
    medicineRepository.findByName(data.getName().trim()).ifPresent(m -> {
      if (!m.getId().equals(medicine.getId())) {
        throw new ConflictException(
            Collections.singletonList("err.medicines.medicine-already-exists"));
      }
    });
    diseaseRepository.findById(data.getDiseaseId())
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err.diseases.disease-not-found")));
    BeanUtils.copyProperties(data, medicine);
    medicineRepository.save(medicine);
    return medicine;
  }

  @Override
  public Medicine detail(Long id) {
    return medicineRepository.findById(id).orElseThrow(()
        -> new NotFoundException(Collections.singletonList("err.medicines.medicine-not-found")));
  }

  @Override
  public void remove(Long id) {
    medicineRepository.findById(id).orElseThrow(()
        -> new NotFoundException(Collections.singletonList("err.medicines.medicine-not-found")));
    List<MedicalRecordMedicine> medicineMedicalRecords = medicalRecordMedicineRepository
        .findAllByMedicineId(id);
    if (!CollectionUtils.isEmpty(medicineMedicalRecords)) {
      throw new ConflictException(
          Collections.singletonList("err.medicines.cannot-remove-medicine"));
    }
    medicineRepository.deleteById(id);
  }
}
