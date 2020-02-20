package vn.mavn.patientservice.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.mavn.patientservice.dto.MedicalRecordAddDto;
import vn.mavn.patientservice.dto.MedicalRecordEditDto;
import vn.mavn.patientservice.dto.MedicineMappingDto;
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
import vn.mavn.patientservice.util.TokenUtils;

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
  @Autowired
  private HttpServletRequest httpServletRequest;

  @Override
  public MedicalRecord addNew(MedicalRecordAddDto medicalRecordAddDto) {
    //TODO: validation data
    // can call sang uaa de valid user_id, user_code
    Long userId = Long.parseLong(TokenUtils.getUserIdFromToken(httpServletRequest));
    String userCode = TokenUtils.getUserCodeFromToken(httpServletRequest);
    validationData(medicalRecordAddDto.getPatientId(), medicalRecordAddDto.getAdvertisingSourceId(),
        medicalRecordAddDto.getClinicId(), medicalRecordAddDto.getAdvisoryStatusCode(),
        medicalRecordAddDto.getDiseaseId());
    MedicalRecord medicalRecord = new MedicalRecord();
    medicalRecord.setUserId(userId);
    medicalRecord.setUserCode(userCode);
    BeanUtils.copyProperties(medicalRecordAddDto, medicalRecord);
    medicalRecordRepository.save(medicalRecord);
    if (!CollectionUtils.isEmpty(medicalRecordAddDto.getMedicineDtos())) {
      mappingMedicalRecordMedicine(medicalRecordAddDto.getMedicineDtos(), medicalRecord.getId());
    }
    return medicalRecord;
  }

  @Override
  public MedicalRecord editMedicalRecord(MedicalRecordEditDto medicalRecordEditDto) {
    //TODO: check exist medical record
    MedicalRecord medicalRecord = medicalRecordRepository
        .findActiveById(medicalRecordEditDto.getId()).orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-medical-record-not-found")));
    validationData(medicalRecordEditDto.getPatientId(),
        medicalRecordEditDto.getAdvertisingSourceId(),
        medicalRecordEditDto.getClinicId(), medicalRecordEditDto.getAdvisoryStatusCode(),
        medicalRecordEditDto.getDiseaseId());
    Long userId = Long.parseLong(TokenUtils.getUserIdFromToken(httpServletRequest));
    String userCode = TokenUtils.getUserCodeFromToken(httpServletRequest);
    BeanUtils.copyProperties(medicalRecordEditDto, medicalRecord);
    medicalRecord.setUserCode(userCode);
    medicalRecord.setUserId(userId);
    List<MedicalRecordMedicine> medicalRecordMedicines = recordMedicineRepository
        .findAllByMedicalRecordId(medicalRecordEditDto.getId());
    recordMedicineRepository.deleteAll(medicalRecordMedicines);
    if (!CollectionUtils.isEmpty(medicalRecordEditDto.getMedicineDtos())) {
      mappingMedicalRecordMedicine(medicalRecordEditDto.getMedicineDtos(), medicalRecord.getId());
    }
    return medicalRecordRepository.save(medicalRecord);
  }

  private void mappingMedicalRecordMedicine(List<MedicineMappingDto> medicineDtos,
      Long medicalRecordId) {
    //TODO: mapping medical_record and medicine
    List<MedicalRecordMedicine> medicalRecordMedicines = new ArrayList<>();
    medicineDtos.forEach(medicineDto -> {
      medecineRepo.findActiveById(medicineDto.getMedicineId())
          .orElseThrow(() ->
              new NotFoundException(Collections.singletonList("err.medicines.medicine-not-found")));
      medicalRecordMedicines.add(
          MedicalRecordMedicine.builder().medicalRecordId(medicalRecordId)
              .medicineId(medicineDto.getMedicineId()).qty(medicineDto.getQty()).build());
    });
    recordMedicineRepository.saveAll(medicalRecordMedicines);
  }

  private void validationData(Long patientId, Long advertId, Long clinicId, String advisoryCode,
      Long diseaseId) {
    // valid patient
    patientRepository.findActiveById(patientId)
        .orElseThrow(
            () -> new NotFoundException(Collections.singletonList("err-patient-not-found")));
    // valid advertising source
    advertisingSourceRepository.findActiveById(advertId)
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-advertising-not-found")));
    // valid clinic
    clinicRepository.findActiveById(clinicId)
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-clinic-not-found")));
    // valid advisory_status - by code
    consultingStatusRepository.findByCode(advisoryCode)
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-advisory-not-found")));
    // valid disease
    diseaseRepository.findActiveById(diseaseId)
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-disease-not-found")));
  }
}
