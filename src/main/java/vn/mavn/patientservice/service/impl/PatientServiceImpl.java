package vn.mavn.patientservice.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.mavn.patientservice.dto.MedicineMappingDto;
import vn.mavn.patientservice.dto.PatientAddDto;
import vn.mavn.patientservice.dto.PatientEditDto;
import vn.mavn.patientservice.dto.UpdatePatientDto;
import vn.mavn.patientservice.dto.qobject.QueryPatientDto;
import vn.mavn.patientservice.entity.Disease;
import vn.mavn.patientservice.entity.MedicalRecord;
import vn.mavn.patientservice.entity.MedicalRecordMedicine;
import vn.mavn.patientservice.entity.Patient;
import vn.mavn.patientservice.exception.BadRequestException;
import vn.mavn.patientservice.exception.ConflictException;
import vn.mavn.patientservice.exception.NotFoundException;
import vn.mavn.patientservice.repository.AdvertisingSourceRepository;
import vn.mavn.patientservice.repository.ClinicDiseaseRepository;
import vn.mavn.patientservice.repository.ClinicRepository;
import vn.mavn.patientservice.repository.ClinicUserRepository;
import vn.mavn.patientservice.repository.ConsultingStatusRepository;
import vn.mavn.patientservice.repository.DiseaseRepository;
import vn.mavn.patientservice.repository.MedicalRecordMedicineRepository;
import vn.mavn.patientservice.repository.MedicalRecordRepository;
import vn.mavn.patientservice.repository.MedicineRepository;
import vn.mavn.patientservice.repository.PatientRepository;
import vn.mavn.patientservice.repository.spec.PatientSpec;
import vn.mavn.patientservice.service.PatientService;
import vn.mavn.patientservice.util.TokenUtils;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {

  @Autowired
  private PatientRepository patientRepository;

  @Autowired
  private MedicalRecordRepository medicalRecordRepository;

  @Autowired
  private HttpServletRequest httpServletRequest;

  @Autowired
  private AdvertisingSourceRepository advertisingSourceRepository;

  @Autowired
  private ClinicRepository clinicRepository;

  @Autowired
  private ConsultingStatusRepository consultingStatusRepository;

  @Autowired
  private DiseaseRepository diseaseRepository;

  @Autowired
  private MedicineRepository medicineRepository;

  @Autowired
  private MedicalRecordMedicineRepository recordMedicineRepository;

  @Autowired
  private ClinicUserRepository clinicUserRepository;

  @Autowired
  private ClinicDiseaseRepository clinicDiseaseRepository;

  @Override
  public Patient addNew(PatientAddDto patientAddDto) {
    //TODO: check at least have 1 phone number.
    if (StringUtils.isBlank(patientAddDto.getOtherPhone()) && StringUtils
        .isBlank(patientAddDto.getPhone()) && StringUtils.isBlank(patientAddDto.getZaLoPhone())) {
      throw new BadRequestException(
          Collections.singletonList("err-patient-phone-number-is-mandatory"));
    }
    Patient patient = new Patient();
    BeanUtils.copyProperties(patientAddDto, patient);
    Long userId = Long.parseLong(TokenUtils.getUserIdFromToken(httpServletRequest));
    patient.setCreatedBy(userId);
    patient.setUpdatedBy(userId);

    return patientRepository.save(patient);
  }

  @Override
  public Patient editPatient(PatientEditDto patientEditDto) {
    //TODO: check patient exist?
    Patient patient = patientRepository.findActiveById(patientEditDto.getId())
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-patient-not-found")));
    //TODO: check at least have 1 phone number.
    if (StringUtils.isBlank(patientEditDto.getOtherPhone()) && StringUtils
        .isBlank(patientEditDto.getPhone()) && StringUtils.isBlank(patientEditDto.getZaLoPhone())) {
      throw new BadRequestException(
          Collections.singletonList("err-patient-phone-number-is-mandatory"));
    }
    BeanUtils.copyProperties(patientEditDto, patient);
    Long userId = Long.parseLong(TokenUtils.getUserIdFromToken(httpServletRequest));
    patient.setUpdatedBy(userId);
    return patientRepository.save(patient);
  }

  @Override
  public Patient getById(Long id) {
    //TODO: check patient exist?
    return patientRepository.findActiveById(id)
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-patient-not-found")));
  }

  @Override
  public Page<Patient> findAll(QueryPatientDto queryPatientDto, Pageable pageable) {
    return patientRepository.findAll(PatientSpec.findAllPatient(queryPatientDto), pageable);
  }

  @Override
  public void delete(Long id) {
    //TODO: check patient exist?
    Patient patient = patientRepository
        .findById(id).orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-patient-not-found")));
    //TODO: valid advertising used or not
    List<MedicalRecord> medicalRecords = medicalRecordRepository
        .findByPatientId(patient.getId());
    if (!CollectionUtils.isEmpty(medicalRecords)) {
      throw new ConflictException(
          Collections.singletonList("err-patient-delete-not-successfully"));
    }
    patientRepository.deletePatient(patient.getId());
  }

  @Override
  public Patient updatePatientAndMedicalRecordForCounselor(UpdatePatientDto data) {

    //validationData (nguon quang cao, phong kham, tinh trang tu van, danh sach benh)
    validationData(data.getAdvertisingSourceId(), data.getConsultingStatusCode(),
        data.getDiseaseIds());
    //lay used tu token
    Long userId = Long.parseLong(TokenUtils.getUserIdFromToken(httpServletRequest));
    String userCode = TokenUtils.getUserCodeFromToken(httpServletRequest);

    //valid danh sach loai benh cua phong kham cua nhan vien dang dang nhap
    Long clinicId = clinicUserRepository.findClinicIdByUserId(userId);
    List<Long> diseaseIds = clinicDiseaseRepository.findAllByClinicId(clinicId);
    if (!data.getDiseaseIds().containsAll(diseaseIds)) {
      throw new NotFoundException(
          Collections.singletonList("err-disease-not-found"));
    }
    //patient
    Patient patient = new Patient();
    BeanUtils.copyProperties(data, patient);
    patientRepository.save(patient);

    //MedicalRecord
    MedicalRecord medicalRecord = new MedicalRecord();
    BeanUtils.copyProperties(data, medicalRecord);
    medicalRecord.setUserCode(userCode);

    medicalRecordRepository.save(medicalRecord);

    //vi thuoc cua loai benh + so luong
    if (!CollectionUtils.isEmpty(data.getMedicineDtos())) {
      mappingMedicalRecordMedicine(data.getMedicineDtos(), medicalRecord.getId());
    }
    
    return patient;
  }

  private void mappingMedicalRecordMedicine(List<MedicineMappingDto> data, Long medicalRecordId) {
    // mapping medical_record and medicine
    List<MedicalRecordMedicine> medicalRecordMedicines = new ArrayList<>();
    if (!CollectionUtils.isEmpty(data)) {
      data.forEach(medicineDto -> {
        medicineRepository.findActiveById(medicineDto.getMedicineId())
            .orElseThrow(() ->
                new NotFoundException(
                    Collections.singletonList("err.medicines.medicine-not-found")));
        medicalRecordMedicines.add(
            MedicalRecordMedicine.builder().medicalRecordId(medicalRecordId)
                .medicineId(medicineDto.getMedicineId()).qty(medicineDto.getQty()).build());
      });
    }
    recordMedicineRepository.saveAll(medicalRecordMedicines);
  }

  private void validationData(Long advertId, String consultingCode, List<Long> diseaseIds) {
    // valid advertising source
    advertisingSourceRepository.findActiveById(advertId)
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-advertising-not-found")));
    // valid advisory_status - by code
    consultingStatusRepository.findByCode(consultingCode)
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-advisory-not-found")));

    // valid disease cua phong kham cua nhan vien dang nhap
    List<Disease> diseases = diseaseRepository.findAllByIdIn(diseaseIds);
    if (CollectionUtils.isEmpty(diseases)) {
      throw new NotFoundException(
          Collections.singletonList("err-disease-not-found"));
    }
  }
}

