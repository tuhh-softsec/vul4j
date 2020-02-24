package vn.mavn.patientservice.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.mavn.patientservice.dto.ClinicDto;
import vn.mavn.patientservice.dto.ClinicDto.DoctorDto;
import vn.mavn.patientservice.dto.DiseaseDto;
import vn.mavn.patientservice.dto.MedicalRecordAddDto;
import vn.mavn.patientservice.dto.MedicalRecordAddForEmpClinicDto;
import vn.mavn.patientservice.dto.MedicalRecordDto;
import vn.mavn.patientservice.dto.MedicalRecordDto.AdvertisingSourceDto;
import vn.mavn.patientservice.dto.MedicalRecordDto.ConsultingStatusDto;
import vn.mavn.patientservice.dto.MedicalRecordDto.PatientDto;
import vn.mavn.patientservice.dto.MedicalRecordEditDto;
import vn.mavn.patientservice.dto.MedicalRecordEditForEmpClinicDto;
import vn.mavn.patientservice.dto.MedicineMappingDto;
import vn.mavn.patientservice.dto.qobject.QueryMedicalRecordDto;
import vn.mavn.patientservice.entity.AdvertisingSource;
import vn.mavn.patientservice.entity.Clinic;
import vn.mavn.patientservice.entity.ConsultingStatus;
import vn.mavn.patientservice.entity.Disease;
import vn.mavn.patientservice.entity.Doctor;
import vn.mavn.patientservice.entity.MedicalRecord;
import vn.mavn.patientservice.entity.MedicalRecordMedicine;
import vn.mavn.patientservice.entity.Medicine;
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
import vn.mavn.patientservice.repository.DoctorRepository;
import vn.mavn.patientservice.repository.MedicalRecordMedicineRepository;
import vn.mavn.patientservice.repository.MedicalRecordRepository;
import vn.mavn.patientservice.repository.MedicineRepository;
import vn.mavn.patientservice.repository.PatientRepository;
import vn.mavn.patientservice.repository.spec.MedicalRecordSpec;
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
  private MedicineRepository medicineRepository;
  @Autowired
  private MedicalRecordMedicineRepository recordMedicineRepository;
  @Autowired
  private HttpServletRequest httpServletRequest;
  @Autowired
  private DoctorRepository doctorRepository;
  @Autowired
  private ClinicUserRepository clinicUserRepository;
  @Autowired
  private ClinicDiseaseRepository clinicDiseaseRepository;

  @Override
  public MedicalRecord addForEmp(MedicalRecordAddDto medicalRecordAddDto) {
    //TODO: validation data
    validationData(medicalRecordAddDto.getAdvertisingSourceId(),
        medicalRecordAddDto.getClinicId(), medicalRecordAddDto.getConsultingStatusCode());

    Long userId = Long.parseLong(TokenUtils.getUserIdFromToken(httpServletRequest));
    Patient patient = new Patient();
    BeanUtils.copyProperties(medicalRecordAddDto.getPatientAddDto(), patient);
    patient.setIsActive(true);
    patient.setCreatedBy(userId);
    patient.setUpdatedBy(userId);
    patientRepository.save(patient);
    //TODO: get user_id, user_code from access_token.
    String userCode = TokenUtils.getUserCodeFromToken(httpServletRequest);
    MedicalRecord medicalRecord = new MedicalRecord();
    medicalRecord.setCreatedBy(userId);
    medicalRecord.setUserCode(userCode);
    medicalRecord.setPatientId(patient.getId());
    medicalRecord.setUpdatedBy(userId);
    BeanUtils.copyProperties(medicalRecordAddDto, medicalRecord);
    medicalRecord.setAdvisoryDate(LocalDateTime.now());
    medicalRecordRepository.save(medicalRecord);
    return medicalRecord;
  }

  @Override
  public MedicalRecordDto getById(Long id) {
    //TODO valid medical record
    MedicalRecord medicalRecord = medicalRecordRepository.findActiveById(id)
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-medical-record-not-found")));
    MedicalRecordDto medicalRecordDto = new MedicalRecordDto();
    BeanUtils.copyProperties(medicalRecord, medicalRecordDto);
    setValueForDto(medicalRecord, medicalRecordDto);
    return medicalRecordDto;
  }

  @Override
  public Page<MedicalRecordDto> findAll(QueryMedicalRecordDto queryMedicalRecordDto,
      Pageable pageable) {
    Page<MedicalRecord> medicalRecords = medicalRecordRepository
        .findAll(MedicalRecordSpec.findAllMedicines(queryMedicalRecordDto), pageable);
    Page<MedicalRecordDto> medicalRecordDtos;
    if (CollectionUtils.isEmpty(medicalRecords.getContent())) {
      return Page.empty(pageable);
    } else {
      medicalRecordDtos = medicalRecords.map(medicalRecord -> {
        MedicalRecordDto medicalRecordDto = new MedicalRecordDto();
        BeanUtils.copyProperties(medicalRecord, medicalRecordDto);
        setValueForDto(medicalRecord, medicalRecordDto);
        return medicalRecordDto;
      });
    }
    return medicalRecordDtos;
  }

  @Override
  public MedicalRecord addForEmpClinic(
      MedicalRecordAddForEmpClinicDto medicalRecordAddDto) {
    //TODO: validation data
    validationData(medicalRecordAddDto.getAdvertisingSourceId(),
        medicalRecordAddDto.getClinicId(), medicalRecordAddDto.getConsultingStatusCode());
    //TODO: check totalAmount = cod + tranfer
    if (!medicalRecordAddDto.getTotalAmount().equals(medicalRecordAddDto.getTransferAmount()
        .add(medicalRecordAddDto.getCodAmount()))) {
      throw new BadRequestException(
          Collections.singletonList("err.medicines.total-amount-not-equal-cod-and-tranfer-amount"));
    }

    //TODO: get user_id, user_code from access_token.
    Long userId = Long.parseLong(TokenUtils.getUserIdFromToken(httpServletRequest));
    //TODO: add new patient from info medical_record
    Patient patient = new Patient();
    patient.setCreatedBy(userId);
    patient.setUpdatedBy(userId);
    BeanUtils.copyProperties(medicalRecordAddDto.getPatientAddDto(), patient);
    patient.setIsActive(true);
    patientRepository.save(patient);
    String userCode = TokenUtils.getUserCodeFromToken(httpServletRequest);
    MedicalRecord medicalRecord = new MedicalRecord();
    medicalRecord.setUserCode(userCode);
    medicalRecord.setCreatedBy(userId);
    medicalRecord.setUpdatedBy(userId);
    medicalRecord.setPatientId(patient.getId());
    BeanUtils.copyProperties(medicalRecordAddDto, medicalRecord);
    medicalRecord.setAdvisoryDate(LocalDateTime.now());
    medicalRecord.setExaminationTimes(1L);
    medicalRecord.setExaminationDate(LocalDateTime.now());
    medicalRecordRepository.save(medicalRecord);
    if (!CollectionUtils.isEmpty(medicalRecordAddDto.getMedicineDtos())) {
      mappingMedicalRecordMedicine(medicalRecordAddDto.getMedicineDtos(), medicalRecord.getId());
    }
    return medicalRecord;
  }

  @Override
  public MedicalRecord editForEmpClinic(MedicalRecordEditForEmpClinicDto data) {

    //lay used tu token
    Long userId = Long.parseLong(TokenUtils.getUserIdFromToken(httpServletRequest));

    Patient patientExist = patientRepository.findActiveById(data.getPatientEditDto().getId())
        .orElseThrow(
            () -> new NotFoundException(Collections.singletonList("err-patient-not-found")));
    //valid danh sach loai benh cua phong kham cua nhan vien phong kham
    Long clinicId = clinicUserRepository.findClinicIdByUserId(userId);
    List<Long> diseaseIds = clinicDiseaseRepository.findAllByClinicId(clinicId);
    if (!data.getDiseaseIds().containsAll(diseaseIds)) {
      throw new NotFoundException(
          Collections.singletonList("err-disease-not-found"));
    }
    //validationData(nguon quang cao, phong kham, tinh trang tu van)
    validationData(data.getAdvertisingSourceId(), clinicId, data.getConsultingStatusCode());

    //patient
    BeanUtils.copyProperties(data.getPatientEditDto(), patientExist);
    patientExist.setIsActive(data.getIsActive());

    //TODO: check totalAmount = cod + tranfer
    if (!data.getTotalAmount().equals(data.getTransferAmount()
        .add(data.getCodAmount()))) {
      throw new BadRequestException(
          Collections.singletonList("err.medicines.total-amount-not-equal-cod-and-tranfer-amount"));
    }

    patientRepository.save(patientExist);

    MedicalRecord medicalRecord = new MedicalRecord();
    //medical record
    MedicalRecord medicalRecordExist = medicalRecordRepository
        .findMedicalRecordByPatientId(patientExist.getId());

    BeanUtils.copyProperties(data, medicalRecord);
    medicalRecord.setAdvisoryDate(medicalRecordExist.getAdvisoryDate());
    medicalRecord.setUserCode(medicalRecordExist.getUserCode());
    medicalRecord.setExaminationTimes(medicalRecordExist.getExaminationTimes());
    medicalRecord.setPatientId(patientExist.getId());
    if (medicalRecord.getConsultingStatusCode().equals("TTTV001")) {
      medicalRecord.setExaminationDate(LocalDateTime.now());
    } else {
      medicalRecord.setExaminationDate(null);
    }
    medicalRecord.setClinicId(medicalRecordExist.getClinicId());
    medicalRecord.setCreatedBy(userId);
    medicalRecord.setUpdatedBy(userId);
    medicalRecordRepository.save(medicalRecord);

    //vi thuoc cua loai benh + so luong
    if (!CollectionUtils.isEmpty(data.getMedicineDtos())) {
      mappingMedicalRecordMedicine(data.getMedicineDtos(), medicalRecord.getId());
    }
    return medicalRecord;
  }

  @Override
  public MedicalRecord update(MedicalRecordEditDto data) {
    MedicalRecord medicalRecord = medicalRecordRepository.findById(data.getId())
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err.medical-records.medical-record-not-found")));
    if (!medicalRecord.getPatientId().equals(data.getPatientId())) {
      throw new ConflictException(
          Collections.singletonList("err.medical-records.patient-info-not-match"));
    }
    validationData(data.getAdvertisingSourceId(), data.getClinicId(),
        data.getConsultingStatusCode());
    List<MedicineMappingDto> medicineList = data.getMedicineDtos();
    if (!CollectionUtils.isEmpty(medicineList)) {
      List<Long> medicines = medicineRepository
          .findAllByIdIn(data.getMedicineDtos().stream()
              .map(MedicineMappingDto::getMedicineId)
              .collect(Collectors.toList()))
          .stream().map(Medicine::getId).collect(Collectors.toList());
      if (!medicines.containsAll(medicineList)) {
        throw new NotFoundException(Collections.singletonList("err.medicines.medicine-not-found"));
      }
      medicineList.forEach(medicine -> {
        if (medicine.getQty() < 0) {
          throw new BadRequestException(
              Collections.singletonList("err.medicines.quantity-must-be-positive"));
        }
      });
      mappingMedicalRecordMedicine(medicineList, medicalRecord.getId());
    }
    // TODO: we can optimise this function:
    //  1. Get token from request header.
    //  2. Using method getValueByKeyInTheToken from Oauth2TokenUtils then pass desire parameter
    // So then we will not have to retrieve token 2 times
    Long userId = Long.parseLong(TokenUtils.getUserIdFromToken(httpServletRequest));
    String userCode = TokenUtils.getUserCodeFromToken(httpServletRequest);
    BeanUtils.copyProperties(data, medicalRecord);
    medicalRecord.setUserCode(userCode);
    medicalRecord.setUpdatedBy(userId);
    medicalRecord.setExaminationTimes(medicalRecord.getExaminationTimes() + 1);
    medicalRecordRepository.save(medicalRecord);
    return medicalRecord;
  }


  private void mappingMedicalRecordMedicine(List<MedicineMappingDto> medicineDtos,
      Long medicalRecordId) {
    //TODO: mapping medical_record and medicine
    List<MedicalRecordMedicine> medicalRecordMedicines = new ArrayList<>();
    if (!CollectionUtils.isEmpty(medicineDtos)) {
      medicineDtos.forEach(medicineDto -> {
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

  private void validationData(Long advertId, Long clinicId, String consultingCode) {
    // valid advertising source
    advertisingSourceRepository.findActiveById(advertId)
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-advertising-not-found")));
    // valid clinic
    clinicRepository.findActiveById(clinicId)
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-clinic-not-found")));
    // valid advisory_status - by code
    consultingStatusRepository.findByCode(consultingCode)
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-advisory-not-found")));
  }

  private void setValueForDto(MedicalRecord medicalRecord, MedicalRecordDto medicalRecordDto) {
    // TODO build PatientDto
    Patient patient = patientRepository.findByIdForGetData(medicalRecord.getPatientId());
    if (patient != null) {
      PatientDto patientDto = new PatientDto();
      BeanUtils.copyProperties(patient, patientDto);
      medicalRecordDto.setPatientDto(patientDto);
    }
    //TODO build diseaseDto
    Disease disease = diseaseRepository.findDiseaseById(medicalRecord.getDiseaseId());
    if (disease != null) {
      DiseaseDto diseaseDto = DiseaseDto.builder().id(disease.getId()).name(disease.getName())
          .build();
      medicalRecordDto.setDiseaseDto(diseaseDto);
    }

    //TODO: build AdvertisingSourceDto
    AdvertisingSource advertisingSource = advertisingSourceRepository
        .findByIdForGetData(medicalRecord.getAdvertisingSourceId());
    if (advertisingSource != null) {
      AdvertisingSourceDto advertisingSourceDto = AdvertisingSourceDto.builder()
          .id(advertisingSource.getId()).name(advertisingSource.getName()).build();
      medicalRecordDto.setAdvertisingSourceDto(advertisingSourceDto);
    }

    //TODO: build ClinicDto
    Clinic clinic = clinicRepository.findByIdForGetData(medicalRecord.getClinicId());
    if (clinic != null) {
      ClinicDto clinicDto = ClinicDto.builder().id(clinic.getId()).address(clinic.getAddress())
          .name(clinic.getName())
          .description(clinic.getDescription()).phone(clinic.getPhone()).build();
      Doctor doctor = doctorRepository.findByIdForGetData(clinic.getDoctorId());
      if (doctor != null) {
        clinicDto.setDoctor(DoctorDto.builder().id(doctor.getId()).name(doctor.getName()).build());
      }
      medicalRecordDto.setClinicDto(clinicDto);
    }

    //TODO: build ConsultingStatusDto
    ConsultingStatus consultingStatus = consultingStatusRepository
        .findActiveByCode(medicalRecord.getConsultingStatusCode());
    if (consultingStatus != null) {
      ConsultingStatusDto consultingStatusDto = ConsultingStatusDto.builder()
          .id(consultingStatus.getId()).code(consultingStatus.getCode())
          .name(consultingStatus.getName()).build();
      medicalRecordDto.setConsultingStatusDto(consultingStatusDto);
    }
  }
  
}
