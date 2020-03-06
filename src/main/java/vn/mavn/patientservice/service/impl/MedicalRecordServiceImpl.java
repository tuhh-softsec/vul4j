package vn.mavn.patientservice.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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
import vn.mavn.patientservice.dto.MedicalRecordAddDto;
import vn.mavn.patientservice.dto.MedicalRecordAddForEmpClinicDto;
import vn.mavn.patientservice.dto.MedicalRecordDto;
import vn.mavn.patientservice.dto.MedicalRecordDto.AdvertisingSourceDto;
import vn.mavn.patientservice.dto.MedicalRecordDto.ClinicBranchDto;
import vn.mavn.patientservice.dto.MedicalRecordDto.ConsultingStatusDto;
import vn.mavn.patientservice.dto.MedicalRecordDto.DiseaseForMedicalRecordDto;
import vn.mavn.patientservice.dto.MedicalRecordDto.MedicineDto;
import vn.mavn.patientservice.dto.MedicalRecordDto.PathologyDto;
import vn.mavn.patientservice.dto.MedicalRecordDto.PatientDto;
import vn.mavn.patientservice.dto.MedicalRecordEditDto;
import vn.mavn.patientservice.dto.MedicineMappingDto;
import vn.mavn.patientservice.dto.qobject.QueryMedicalRecordDto;
import vn.mavn.patientservice.dto.qobject.QueryPatientDto;
import vn.mavn.patientservice.entity.AdvertisingSource;
import vn.mavn.patientservice.entity.Clinic;
import vn.mavn.patientservice.entity.ClinicBranch;
import vn.mavn.patientservice.entity.ConsultingStatus;
import vn.mavn.patientservice.entity.Disease;
import vn.mavn.patientservice.entity.Doctor;
import vn.mavn.patientservice.entity.MedicalRecord;
import vn.mavn.patientservice.entity.MedicalRecordMedicine;
import vn.mavn.patientservice.entity.Medicine;
import vn.mavn.patientservice.entity.Patient;
import vn.mavn.patientservice.entity.PatientPathology;
import vn.mavn.patientservice.entity.Province;
import vn.mavn.patientservice.exception.BadRequestException;
import vn.mavn.patientservice.exception.ConflictException;
import vn.mavn.patientservice.exception.NotFoundException;
import vn.mavn.patientservice.repository.AdvertisingSourceRepository;
import vn.mavn.patientservice.repository.ClinicBranchRepository;
import vn.mavn.patientservice.repository.ClinicDiseaseRepository;
import vn.mavn.patientservice.repository.ClinicRepository;
import vn.mavn.patientservice.repository.ClinicUserRepository;
import vn.mavn.patientservice.repository.ConsultingStatusRepository;
import vn.mavn.patientservice.repository.DiseaseRepository;
import vn.mavn.patientservice.repository.DoctorRepository;
import vn.mavn.patientservice.repository.MedicalRecordMedicineRepository;
import vn.mavn.patientservice.repository.MedicalRecordRepository;
import vn.mavn.patientservice.repository.MedicineRepository;
import vn.mavn.patientservice.repository.PathologyRepository;
import vn.mavn.patientservice.repository.PatientPathologyRepository;
import vn.mavn.patientservice.repository.PatientRepository;
import vn.mavn.patientservice.repository.ProvinceRepository;
import vn.mavn.patientservice.repository.spec.MedicalRecordSpec;
import vn.mavn.patientservice.repository.spec.PatientSpec;
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

  @Autowired
  private MedicalRecordMedicineRepository medicalRecordMedicineRepository;

  @Autowired
  private ProvinceRepository provinceRepository;

  @Autowired
  private ClinicBranchRepository clinicBranchRepository;
  @Autowired
  private PathologyRepository pathologyRepository;
  @Autowired
  private PatientPathologyRepository patientPathologyRepository;

  @Override
  public MedicalRecord addForEmp(MedicalRecordAddDto medicalRecordAddDto) {
    //TODO: validation data
    validationData(medicalRecordAddDto.getAdvertisingSourceId(),
        medicalRecordAddDto.getClinicId(), medicalRecordAddDto.getConsultingStatusCode(),
        medicalRecordAddDto.getClinicBranchId());

    // Validate pathology
    validatePathology(medicalRecordAddDto.getPatientDto().getPathologyIds());

    Long userId = Long.parseLong(TokenUtils.getUserIdFromToken(httpServletRequest));
    MedicalRecord medicalRecord;
    String userCode = TokenUtils.getUserCodeFromToken(httpServletRequest);
    //TODO: check if have patient_id -> action re-examination only add new medical_record
    if (medicalRecordAddDto.getPatientDto().getId() != null) {
      Patient patient = patientRepository
          .findActiveById(medicalRecordAddDto.getPatientDto().getId())
          .orElseThrow(() -> new NotFoundException(
              Collections.singletonList("err-patient-not-found")));
      BeanUtils.copyProperties(medicalRecordAddDto.getPatientDto(), patient);
      patient.setIsActive(true);
      patientRepository.save(patient);

      // Save patient pathology
      savePatientPathology(patient.getId(), medicalRecordAddDto.getPatientDto().getPathologyIds());

      //TODO: find list medical_record by patient_id
      medicalRecord = mapMedicalRecordForEmp(medicalRecordAddDto, userId, userCode,
          patient.getId());

    } else {
      Patient patient = new Patient();
      BeanUtils.copyProperties(medicalRecordAddDto.getPatientDto(), patient);
      patient.setIsActive(true);
      patient.setCreatedBy(userId);
      patient.setUpdatedBy(userId);
      patientRepository.save(patient);

      // Save patient pathology
      savePatientPathology(patient.getId(), medicalRecordAddDto.getPatientDto().getPathologyIds());

      medicalRecord = mapMedicalRecordForEmp(medicalRecordAddDto, userId, userCode,
          patient.getId());
    }
    medicalRecord.setIsActive(true);
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
    List<Long> patientIds = handlePatientFilters(queryMedicalRecordDto);
    if (CollectionUtils.isEmpty(patientIds)) {
      return Page.empty(pageable);
    }
    Page<MedicalRecord> medicalRecords = medicalRecordRepository
        .findAll(MedicalRecordSpec.findAllMedicines(queryMedicalRecordDto, patientIds), pageable);
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
      MedicalRecordAddForEmpClinicDto data) {
    //TODO: validation data
    validationData(data.getAdvertisingSourceId(),
        data.getClinicId(), data.getConsultingStatusCode(), data.getClinicBranchId());

    // Validate pathologies
    validatePathology(data.getPatientDto().getPathologyIds());

    MedicalRecord medicalRecord;
    //TODO: get user_id, user_code from access_token.
    Long userId = Long.parseLong(TokenUtils.getUserIdFromToken(httpServletRequest));
    String userCode = TokenUtils.getUserCodeFromToken(httpServletRequest);
    //TODO: check if have patient_id -> action re-examination only add new medical_record
    if (data.getPatientDto().getId() != null) {
      Patient patient = setUpdatePatientForEmpClinic(data);

      // Save pathologies of patient
      savePatientPathology(patient.getId(), data.getPatientDto().getPathologyIds());

      //TODO: find list medical_record by patient_id
      medicalRecord = mappingMedicalRecordForEmpClinic(userCode, userId, patient.getId(),
          data);
    } else {
      //TODO: add new patient from info medical_record
      Patient patient = new Patient();
      patient.setCreatedBy(userId);
      patient.setUpdatedBy(userId);
      BeanUtils.copyProperties(data.getPatientDto(), patient);
      patient.setIsActive(true);
      patientRepository.save(patient);

      // Save pathologies of patient
      savePatientPathology(patient.getId(), data.getPatientDto().getPathologyIds());

      medicalRecord = mappingMedicalRecordForEmpClinic(userCode, userId, patient.getId(),
          data);
    }

    medicalRecord.setIsActive(true);
    setPaymentInfo(medicalRecord, data.getTotalAmount(), data.getCodAmount(),
        data.getTransferAmount());
    medicalRecordRepository.save(medicalRecord);
    if (!CollectionUtils.isEmpty(data.getMedicineDtos())) {
      mappingMedicalRecordMedicine(data.getMedicineDtos(),
          medicalRecord.getId());
    }

    return medicalRecord;
  }

  @Override
  public MedicalRecord editForEmpClinic(MedicalRecordEditDto data) {
    if (data.getDiseaseId() == null) {
      throw new BadRequestException(
          Collections.singletonList("err-medical-record-disease-id-is-mandatory"));
    }
    if (CollectionUtils.isEmpty(data.getMedicineDtos())) {
      throw new BadRequestException(
          Collections.singletonList("err-medical-record-medicineDtos-is-mandatory"));
    }
    MedicalRecord existedMedicalRecord = medicalRecordRepository.findActiveById(data.getId())
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-medical-record-not-found")));

    //lay used tu token
    Long userId = Long.parseLong(TokenUtils.getUserIdFromToken(httpServletRequest));

    //valid phong kham cua nhan vien phong kham
    Long clinicId = clinicUserRepository.findClinicIdByUserId(userId);
    if (clinicId == null || !clinicId.equals(existedMedicalRecord.getClinicId())) {
      throw new NotFoundException(
          Collections.singletonList("err.medical-record.permission-denied"));
    }

    //valid danh sach loai benh cua phong kham cua nhan vien phong kham
    List<Long> diseaseIds = clinicDiseaseRepository.findAllByClinicId(clinicId);
    if (!diseaseIds.contains(data.getDiseaseId())) {
      throw new NotFoundException(Collections.singletonList("err-disease-not-found"));
    }

    //validationData(nguon quang cao, phong kham, tinh trang tu van)
    validationData(data.getAdvertisingSourceId(), clinicId, data.getConsultingStatusCode(),
        data.getClinicBranchId());

    // Validate pathologies
    validatePathology(data.getPatientDto().getPathologyIds());

    Patient patientExist = patientRepository.findActiveById(data.getPatientDto().getId())
        .orElseThrow(
            () -> new NotFoundException(Collections.singletonList("err-patient-not-found")));

    //patient
    BeanUtils.copyProperties(data.getPatientDto(), patientExist);
    patientExist.setIsActive(true);

    patientRepository.save(patientExist);

    // Save pathologies of patient
    savePatientPathology(patientExist.getId(), data.getPatientDto().getPathologyIds());

    //medical record
    MedicalRecord medicalRecord = medicalRecordRepository
        .findActiveById(data.getId()).get();

    BeanUtils.copyProperties(data, medicalRecord);
    if (medicalRecord.getConsultingStatusCode().equals("TTTV001")) {
      medicalRecord.setExaminationDate(LocalDateTime.now());
    } else {
      medicalRecord.setExaminationDate(null);
    }
    medicalRecord.setClinicId(medicalRecord.getClinicId());
    medicalRecord.setIsActive(true);
    medicalRecord.setUpdatedBy(userId);
    setPaymentInfo(medicalRecord, data.getTotalAmount(), data.getCodAmount(),
        data.getTransferAmount());
    medicalRecordRepository.save(medicalRecord);

    //vi thuoc cua loai benh + so luong
    if (!CollectionUtils.isEmpty(data.getMedicineDtos())) {
      mappingMedicalRecordMedicine(data.getMedicineDtos(), medicalRecord.getId());
    }
    return medicalRecord;
  }

  private void savePatientPathology(Long patientId, List<Long> pathologyIds) {
    List<PatientPathology> existedPatientPathologies = patientPathologyRepository
        .findAllByPatientId(patientId);
    patientPathologyRepository.deleteAll(existedPatientPathologies);
    if (!CollectionUtils.isEmpty(pathologyIds)) {
      List<PatientPathology> patientPathologies = new ArrayList<>();
      pathologyIds.forEach(pathologyId -> {
        patientPathologies.add(
            PatientPathology.builder().patientId(patientId).pathologyId(pathologyId)
                .build());
      });
      patientPathologyRepository.saveAll(patientPathologies);
    }
  }

  private void validatePathology(List<Long> pathologyIds) {
    if (!CollectionUtils.isEmpty(pathologyIds)) {
      pathologyIds.forEach(pathologyId -> {
        pathologyRepository.findById(pathologyId).orElseThrow(() -> new NotFoundException(
            Arrays.asList("err.medical-records.pathology-not-found")));
      });
    }
  }

  /**
   * Find all medical record for exporting report.
   */
  @Override
  public List<MedicalRecordDto> findAllForReport(QueryMedicalRecordDto queryMedicalRecordDto) {
    List<Long> patientIds = handlePatientFilters(queryMedicalRecordDto);
    if (CollectionUtils.isEmpty(patientIds)) {
      return new ArrayList<>();
    }
    List<MedicalRecord> medicalRecords = medicalRecordRepository
        .findAll(MedicalRecordSpec.findAllMedicines(queryMedicalRecordDto, patientIds));
    if (CollectionUtils.isEmpty(medicalRecords)) {
      return Collections.emptyList();
    } else {
      List<MedicalRecordDto> medicalRecordDtos = new ArrayList<>();
      medicalRecords.forEach(medicalRecord -> {
        MedicalRecordDto medicalRecordDto = new MedicalRecordDto();
        BeanUtils.copyProperties(medicalRecord, medicalRecordDto);
        setValueForDto(medicalRecord, medicalRecordDto);
        medicalRecordDtos.add(medicalRecordDto);
      });
      return medicalRecordDtos.stream()
          .sorted(Comparator.comparing(MedicalRecordDto::getAdvisoryDate)).collect(
              Collectors.toList());
    }
  }

  @Override
  public MedicalRecord update(MedicalRecordEditDto data) {
    MedicalRecord medicalRecord = medicalRecordRepository.findById(data.getId())
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err.medical-records.medical-record-not-found")));
    if (!medicalRecord.getPatientId().equals(data.getPatientDto().getId())) {
      throw new ConflictException(
          Collections.singletonList("err.medical-records.patient-info-not-match"));
    }

    validationData(data.getAdvertisingSourceId(), data.getClinicId(),
        data.getConsultingStatusCode(), data.getClinicBranchId());

    // Validate pathologies
    validatePathology(data.getPatientDto().getPathologyIds());

    // TODO: we can optimise this function:
    //  1. Get token from request header.
    //  2. Using method getValueByKeyInTheToken from Oauth2TokenUtils then pass desire parameter
    // So then we will not have to retrieve token 2 times
    Long userId = Long.parseLong(TokenUtils.getUserIdFromToken(httpServletRequest));
    if (!userId.equals(medicalRecord.getCreatedBy())) {
      throw new NotFoundException(
          Collections.singletonList("err.medical-record.permission-denied"));
    }

    if (data.getDiseaseId() != null) {
      diseaseRepository.findActiveById(data.getDiseaseId())
          .orElseThrow(() -> new NotFoundException(
              Collections.singletonList("err.diseases.disease-not-found")));
    }

    if (!CollectionUtils.isEmpty(data.getMedicineDtos())) {
      List<MedicineMappingDto> medicineList = data.getMedicineDtos();
      if (!CollectionUtils.isEmpty(medicineList)) {
        List<Long> medicineIds = medicineList.stream().map(MedicineMappingDto::getMedicineId)
            .collect(Collectors.toList());
        List<Long> medicines = medicineRepository
            .findAllByIdIn(medicineIds)
            .stream().map(Medicine::getId).collect(Collectors.toList());
        if (!medicines.containsAll(medicineIds)) {
          throw new NotFoundException(
              Collections.singletonList("err.medicines.medicine-not-found"));
        }
        medicineList.forEach(medicine -> {
          if (medicine.getQty() < 0) {
            throw new BadRequestException(
                Collections.singletonList("err.medicines.quantity-must-be-positive"));
          }
        });
        recordMedicineRepository.deleteAllByMedicalRecordId(medicalRecord.getId());
        mappingMedicalRecordMedicine(medicineList, medicalRecord.getId());
      }
    }

    Patient patient = patientRepository.findActiveById(data.getPatientDto().getId())
        .orElseThrow(
            () -> new NotFoundException(Collections.singletonList("err-patient-not-found")));
    BeanUtils.copyProperties(data.getPatientDto(), patient);
    patientRepository.save(patient);

    // Save pathologies of patient
    savePatientPathology(patient.getId(), data.getPatientDto().getPathologyIds());

    String userCode = TokenUtils.getUserCodeFromToken(httpServletRequest);
    BeanUtils.copyProperties(data, medicalRecord);
    medicalRecord.setUserCode(userCode);
    medicalRecord.setUpdatedBy(userId);
    medicalRecord.setIsActive(true);
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

  private void validationData(Long advertId, Long clinicId, String consultingCode,
      Long clinicBranchId) {
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
    // valid clinic branch
    if (clinicBranchId != null) {
      clinicBranchRepository.findById(clinicBranchId).orElseThrow(() ->
          new NotFoundException(
              Collections.singletonList("err.clinic-branch.clinic-branch-does-not-exist"))
      );
    }

  }

  private void setValueForDto(MedicalRecord medicalRecord, MedicalRecordDto medicalRecordDto) {
    // TODO build PatientDto
    Patient patient = patientRepository.findByIdForGetData(medicalRecord.getPatientId());
    if (patient != null) {
      PatientDto patientDto = new PatientDto();
      BeanUtils.copyProperties(patient, patientDto);
      Province province = provinceRepository.findByCode(patient.getProvinceCode());
      if (province != null) {
        patientDto.setProvince(province);
      }
      //TODO: build Pathologies
      List<Long> pathologyIds = patientPathologyRepository
          .findAllByPatientId(medicalRecord.getPatientId()).stream()
          .map(PatientPathology::getPathologyId)
          .collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(pathologyIds)) {
        List<PathologyDto> pathologies = pathologyRepository.findAllById(pathologyIds).stream()
            .map(pathology -> PathologyDto.builder()
                .id(pathology.getId())
                .name(pathology.getName())
                .build()).collect(Collectors.toList());
        patientDto.setPathologies(pathologies);
      }
      medicalRecordDto.setPatientDto(patientDto);
    }
    //TODO build diseaseDto
    Disease disease = diseaseRepository.findDiseaseById(medicalRecord.getDiseaseId());
    if (disease != null) {
      DiseaseForMedicalRecordDto diseaseDto = DiseaseForMedicalRecordDto.builder()
          .id(disease.getId()).name(disease.getName())
          .build();
      List<Long> medicineIds = medicalRecordMedicineRepository
          .findAllByMedicalRecordId(medicalRecord.getId()).stream().map(
              MedicalRecordMedicine::getMedicineId).collect(Collectors.toList());
      List<MedicineDto> medicineDtos = new ArrayList<>();
      if (!CollectionUtils.isEmpty(medicineIds)) {
        List<Medicine> medicines = medicineRepository.findAllByIdIn(medicineIds);
        medicines.forEach(medicine -> {
          MedicineDto medicineDto = MedicineDto.builder().id(medicine.getId())
              .name(medicine.getName()).build();
          List<MedicalRecordMedicine> medicalRecordMedicines = medicalRecordMedicineRepository
              .findAllByMedicineId(medicine.getId());
          medicalRecordMedicines.forEach(medicalRecordMedicine -> {
            if (medicalRecord.getId().equals(medicalRecordMedicine.getMedicalRecordId())) {
              medicineDto.setQty(Math.toIntExact(medicalRecordMedicine.getQty()));
            }
          });
          medicineDtos.add(medicineDto);
        });
        diseaseDto.setMedicines(medicineDtos);
      }
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
          .name(clinic.getName()).isActive(clinic.getIsActive())
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

    //TODO: build ClinicBranchDto
    ClinicBranch clinicBranch = clinicBranchRepository
        .findClinicBranchById(medicalRecord.getClinicBranchId());
    if (clinicBranch != null) {
      ClinicBranchDto clinicBranchDto = ClinicBranchDto.builder().id(clinicBranch.getId())
          .name(clinicBranch.getName()).build();
      medicalRecordDto.setClinicBranchDto(clinicBranchDto);
    }

  }

  private MedicalRecord mapMedicalRecordForEmp(MedicalRecordAddDto medicalRecordAddDto,
      Long userId,
      String userCode, Long patientId) {
    MedicalRecord medicalRecord = new MedicalRecord();
    BeanUtils.copyProperties(medicalRecordAddDto, medicalRecord);
    medicalRecord.setCreatedBy(userId);
    medicalRecord.setUserCode(userCode);
    medicalRecord.setPatientId(patientId);
    medicalRecord.setUpdatedBy(userId);
    medicalRecord.setAdvisoryDate(LocalDateTime.now());
    medicalRecord.setExaminationTimes(medicalRecordAddDto.getExaminationTime());
    return medicalRecord;
  }

  private MedicalRecord mappingMedicalRecordForEmpClinic(String userCode, Long userId,
      Long patientId, MedicalRecordAddForEmpClinicDto medicalRecordAddDto) {
    MedicalRecord medicalRecord = new MedicalRecord();
    medicalRecord.setUserCode(userCode);
    medicalRecord.setCreatedBy(userId);
    medicalRecord.setUpdatedBy(userId);
    medicalRecord.setPatientId(patientId);
    BeanUtils.copyProperties(medicalRecordAddDto, medicalRecord);
    medicalRecord.setAdvisoryDate(LocalDateTime.now());
    medicalRecord.setExaminationDate(LocalDateTime.now());
    medicalRecord.setExaminationTimes(medicalRecordAddDto.getExaminationTime());
    return medicalRecord;
  }

  private Patient setUpdatePatientForEmpClinic(
      MedicalRecordAddForEmpClinicDto medicalRecordAddDto) {
    Patient patient = patientRepository
        .findActiveById(medicalRecordAddDto.getPatientDto().getId())
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-patient-not-found")));
    BeanUtils.copyProperties(medicalRecordAddDto.getPatientDto(), patient);
    patient.setIsActive(true);
    patientRepository.save(patient);
    return patient;
  }

  private List<Long> handlePatientFilters(QueryMedicalRecordDto data) {
    Set<Long> patientIds;
    QueryPatientDto queryDto = QueryPatientDto.builder()
        .age(data.getPatientAge())
        .name(data.getPatientName())
        .phoneNumber(data.getPhoneNumber())
        .provinceCode(data.getProvinceCode()).build();
    List<Patient> patients = patientRepository.findAll(PatientSpec.findAllPatient(queryDto));
    patientIds = patients.stream().map(Patient::getId).collect(Collectors.toSet());
    return new ArrayList<>(patientIds);
  }

  private void setPaymentInfo(MedicalRecord medicalRecord, BigDecimal totalAmount,
      BigDecimal transferAmount, BigDecimal codAmount) {
    medicalRecord.setTotalAmount(
        totalAmount != null ? totalAmount : BigDecimal.ZERO);
    medicalRecord.setTransferAmount(
        transferAmount != null ? transferAmount : BigDecimal.ZERO);
    medicalRecord.setCodAmount(
        codAmount != null ? codAmount : BigDecimal.ZERO);
  }
}
