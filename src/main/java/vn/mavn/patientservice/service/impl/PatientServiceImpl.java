package vn.mavn.patientservice.service.impl;

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
import vn.mavn.patientservice.dto.PatientDto;
import vn.mavn.patientservice.dto.PatientInfoDto;
import vn.mavn.patientservice.dto.qobject.QueryPatientDto;
import vn.mavn.patientservice.entity.MedicalRecord;
import vn.mavn.patientservice.entity.Patient;
import vn.mavn.patientservice.entity.Province;
import vn.mavn.patientservice.exception.BadRequestException;
import vn.mavn.patientservice.exception.ConflictException;
import vn.mavn.patientservice.exception.NotFoundException;
import vn.mavn.patientservice.repository.MedicalRecordRepository;
import vn.mavn.patientservice.repository.PatientRepository;
import vn.mavn.patientservice.repository.ProvinceRepository;
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
  private ProvinceRepository provinceRepository;

  @Override
  public Patient addNew(PatientDto patientDto) {
    //TODO: check at least have 1 phone number.
    if (StringUtils.isBlank(patientDto.getOtherPhone()) && StringUtils
        .isBlank(patientDto.getPhone()) && StringUtils
        .isBlank(patientDto.getZaloPhone())) {
      throw new BadRequestException(
          Collections.singletonList("err-patient-phone-number-is-mandatory"));
    }
    Patient patient = new Patient();
    BeanUtils.copyProperties(patientDto, patient);
    Long userId = Long.parseLong(TokenUtils.getUserIdFromToken(httpServletRequest));
    patient.setCreatedBy(userId);
    patient.setUpdatedBy(userId);
    patient.setIsActive(true);

    return patientRepository.save(patient);
  }

  @Override
  public Patient editPatient(PatientDto data) {
    //TODO: check patient exist?
    Patient patient = patientRepository.findActiveById(data.getId())
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-patient-not-found")));
    //TODO: check at least have 1 phone number.
    if (StringUtils.isBlank(data.getOtherPhone()) && StringUtils
        .isBlank(data.getPhone()) && StringUtils.isBlank(data.getZaloPhone())) {
      throw new BadRequestException(
          Collections.singletonList("err-patient-phone-number-is-mandatory"));
    }
    Long userId = Long.parseLong(TokenUtils.getUserIdFromToken(httpServletRequest));
    BeanUtils.copyProperties(data, patient);
    patient.setUpdatedBy(userId);
    patient.setIsActive(true);
    return patientRepository.save(patient);
  }

  @Override
  public PatientInfoDto getById(Long id) {
    //TODO: check patient exist?
    Patient patient = patientRepository.findActiveById(id)
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-patient-not-found")));
    //get info district and province
    return mappingValuePatientInfoDto(patient);
  }

  @Override
  public Page<PatientInfoDto> findAll(QueryPatientDto queryPatientDto, Pageable pageable) {

    Page<Patient> patients = patientRepository
        .findAll(PatientSpec.findAllPatient(queryPatientDto), pageable);
    return patients.map(this::mappingValuePatientInfoDto);
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

  private PatientInfoDto mappingValuePatientInfoDto(Patient patient) {
    PatientInfoDto patientInfoDto = new PatientInfoDto();
    BeanUtils.copyProperties(patient, patientInfoDto);
    if (patient.getProvinceCode() != null) {
      Province province = provinceRepository.findByCode(patient.getProvinceCode());
      if (province != null) {
        patientInfoDto.setProvince(province);
      }
    }
    return patientInfoDto;
  }

}

