package vn.mavn.patientservice.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.mavn.patientservice.dto.MedicalRecordAddDto;
import vn.mavn.patientservice.entity.MedicalRecord;
import vn.mavn.patientservice.entity.MedicalRecordMedicine;
import vn.mavn.patientservice.exception.NotFoundException;
import vn.mavn.patientservice.gracefulshd.MedicalRecordMedicineRepository;
import vn.mavn.patientservice.repository.AdvertisingSourceRepository;
import vn.mavn.patientservice.repository.ClinicRepository;
import vn.mavn.patientservice.repository.ConsultingStatusRepository;
import vn.mavn.patientservice.repository.DiseaseRepository;
import vn.mavn.patientservice.repository.MedicalRecordRepository;
import vn.mavn.patientservice.repository.MedicineRepository;
import vn.mavn.patientservice.repository.PatientRepository;
import vn.mavn.patientservice.service.MedicalRecordService;

@Service
@Transactional
public class MedicalRecordServiceImpl implements MedicalRecordService {

  @Autowired
  private PatientRepository patientRepository;
  @Autowired
  private AdvertisingSourceRepository advertisingSourceRepository;
  @Autowired
  private ClinicRepository clinicRepository;
  @Autowired
  private ConsultingStatusRepository consultingStatusRepository;
  @Autowired
  private DiseaseRepository diseaseRepository;
  @Autowired
  private MedicalRecordRepository medicalRecordRepository;
  @Autowired
  private MedicineRepository medecineRepo;
  @Autowired
  private MedicalRecordMedicineRepository recordMedicineRepository;

  @Override
  public MedicalRecord addNew(MedicalRecordAddDto medicalRecordAddDto) {
    //TODO: validation data
    // can call sang uaa de valid user_id, user_code
    // valid patient
    patientRepository.findActiveById(medicalRecordAddDto.getPatientId())
        .orElseThrow(
            () -> new NotFoundException(Collections.singletonList("err-patient-not-found")));
    // valid advertising source
    advertisingSourceRepository.findActiveById(medicalRecordAddDto.getAdvertisingSourceId())
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-advertising-not-found")));
    // valid clinic
    clinicRepository.findActiveById(medicalRecordAddDto.getClinicId())
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-clinic-not-found")));
    // valid advisory_status - by code
    consultingStatusRepository.findByCode(medicalRecordAddDto.getAdvisoryStatusCode())
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-advisory-not-found")));
    // valid disease
    diseaseRepository.findActiveById(medicalRecordAddDto.getDiseaseId())
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-disease-not-found")));
    MedicalRecord medicalRecord = new MedicalRecord();
    BeanUtils.copyProperties(medicalRecordAddDto, medicalRecord);
    medicalRecordRepository.save(medicalRecord);

    //TODO: mapping medical_record and medicine
    List<MedicalRecordMedicine> medicalRecordMedicines = new ArrayList<>();
    medicalRecordAddDto.getMedicineDtos().forEach(medicineDto -> {
      medecineRepo.findActiveById(medicineDto.getMedicineId())
          .orElseThrow(() ->
              new NotFoundException(Collections.singletonList("err.medicines.medicine-not-found")));
      medicalRecordMedicines.add(
          MedicalRecordMedicine.builder().medicalRecordId(medicalRecord.getId())
              .medicineId(medicineDto.getMedicineId()).qty(medicineDto.getQty()).build());
    });
    recordMedicineRepository.saveAll(medicalRecordMedicines);
    return medicalRecord;
  }
}
