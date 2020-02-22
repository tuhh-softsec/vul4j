package vn.mavn.patientservice.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.mavn.patientservice.dto.DiseaseDto;
import vn.mavn.patientservice.dto.MedicineAddDto;
import vn.mavn.patientservice.dto.MedicineDto;
import vn.mavn.patientservice.dto.MedicineEditDto;
import vn.mavn.patientservice.dto.qobject.QueryMedicineDto;
import vn.mavn.patientservice.entity.Disease;
import vn.mavn.patientservice.entity.MedicalRecordMedicine;
import vn.mavn.patientservice.entity.Medicine;
import vn.mavn.patientservice.entity.MedicineDisease;
import vn.mavn.patientservice.exception.ConflictException;
import vn.mavn.patientservice.exception.NotFoundException;
import vn.mavn.patientservice.gracefulshd.MedicalRecordMedicineRepository;
import vn.mavn.patientservice.repository.DiseaseRepository;
import vn.mavn.patientservice.repository.MedicineDiseaseRepository;
import vn.mavn.patientservice.repository.MedicineRepository;
import vn.mavn.patientservice.repository.spec.MedicineSpec;
import vn.mavn.patientservice.service.MedicineService;
import vn.mavn.patientservice.util.TokenUtils;

@Service
public class MedicineServiceImpl implements MedicineService {

  @Autowired
  private MedicineRepository medicineRepository;

  @Autowired
  private DiseaseRepository diseaseRepository;

  @Autowired
  private MedicalRecordMedicineRepository medicalRecordMedicineRepository;

  @Autowired
  private MedicineDiseaseRepository medicineDiseaseRepository;

  @Autowired
  private HttpServletRequest httpServletRequest;

  @Override
  public Page<Medicine> getAllMedicines(QueryMedicineDto data, Pageable pageable) {
    List<Long> medicineIds = new ArrayList<>();
    if (!CollectionUtils.isEmpty(data.getDiseaseIds())) {
      medicineIds = medicineDiseaseRepository
          .findAllMedicineByDiseaseId(data.getDiseaseIds());
      if (CollectionUtils.isEmpty(medicineIds)) {
        return Page.empty(pageable);
      }
    }
    return medicineRepository.findAll(MedicineSpec.findAllMedicines(data, medicineIds), pageable);
  }

  @Override
  public Medicine add(MedicineAddDto data) {
    medicineRepository.findByName(data.getName().trim()).ifPresent(medicine -> {
      throw new ConflictException(
          Collections.singletonList("err.medicines.medicine-already-exists"));
    });
    validateDiseaseData(data.getDiseaseIds());
    Medicine medicine = new Medicine();
    BeanUtils.copyProperties(data, medicine);
    //Get user logged in ID
    Long loggedInUserId = Long.valueOf(TokenUtils.getUserIdFromToken(httpServletRequest));
    medicine.setCreatedBy(loggedInUserId);
    medicine.setUpdatedBy(loggedInUserId);
    medicineRepository.save(medicine);
    mappingMedicineDisease(medicine, data.getDiseaseIds());
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
    validateDiseaseData(data.getDiseaseIds());
    BeanUtils.copyProperties(data, medicine);
    //Get user logged in ID
    Long loggedInUserId = Long.valueOf(TokenUtils.getUserIdFromToken(httpServletRequest));
    medicine.setUpdatedBy(loggedInUserId);
    medicineRepository.save(medicine);
    mappingMedicineDisease(medicine, data.getDiseaseIds());
    return medicine;
  }

  @Override
  public MedicineDto detail(Long id) {
    Medicine medicine = medicineRepository.findById(id).orElseThrow(()
        -> new NotFoundException(Collections.singletonList("err.medicines.medicine-not-found")));
    List<Disease> diseases = diseaseRepository
        .findAllByIdIn(medicine.getDiseases().stream().map(Disease::getId).collect(
            Collectors.toList()));
    List<DiseaseDto> diseaseList = diseases.stream()
        .map(disease -> DiseaseDto.builder().id(disease.getId()).name(disease.getName()).build())
        .collect(Collectors.toList());
    MedicineDto result = new MedicineDto();
    BeanUtils.copyProperties(medicine, result);
    result.setDiseases(diseaseList);
    return result;
  }

  @Override
  public void remove(Long id) {
    medicineRepository.findById(id).orElseThrow(()
        -> new NotFoundException(Collections.singletonList("err.medicines.medicine-not-found")));

    List<MedicalRecordMedicine> medicineMedicalRecords = medicalRecordMedicineRepository
        .findAllByMedicineId(id);
    List<MedicineDisease> medicineDiseases = medicineDiseaseRepository.findAllByMedicineId(id);
    if (!CollectionUtils.isEmpty(medicineMedicalRecords)
        || !CollectionUtils.isEmpty(medicineDiseases)) {
      throw new ConflictException(
          Collections.singletonList("err.medicines.cannot-remove-medicine"));
    }

    medicineRepository.deleteById(id);
  }

  private void validateDiseaseData(List<Long> diseaseIds) {
    List<Long> existDiseases = diseaseRepository.findAllByIdIn(diseaseIds).stream()
        .map(Disease::getId).collect(Collectors.toList());
    if (!existDiseases.containsAll(diseaseIds)) {
      throw new NotFoundException(Collections.singletonList("err.diseases.disease-not-found"));
    }
  }

  private void mappingMedicineDisease(Medicine medicine, List<Long> diseaseIds) {
    List<MedicineDisease> mappingData = new ArrayList<>();
    diseaseIds.forEach(diseaseId -> mappingData
        .add(MedicineDisease.builder().medicineId(medicine.getId()).diseaseId(diseaseId).build()));
    medicineDiseaseRepository.saveAll(mappingData);
  }
}
