package vn.mavn.patientservice.service.impl;

import java.util.Collections;
import java.util.List;
import javax.transaction.Transactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.mavn.patientservice.dto.PatientAddDto;
import vn.mavn.patientservice.dto.PatientEditDto;
import vn.mavn.patientservice.dto.qobject.QueryPatientDto;
import vn.mavn.patientservice.entity.MedicalRecord;
import vn.mavn.patientservice.entity.Patient;
import vn.mavn.patientservice.exception.BadRequestException;
import vn.mavn.patientservice.exception.ConflictException;
import vn.mavn.patientservice.exception.NotFoundException;
import vn.mavn.patientservice.repository.MedicalRecordRepository;
import vn.mavn.patientservice.repository.PatientRepository;
import vn.mavn.patientservice.repository.spec.PatientSpec;
import vn.mavn.patientservice.service.PatientService;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {

  @Autowired
  private PatientRepository patientRepository;

  @Autowired
  private MedicalRecordRepository medicalRecordRepository;

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
}

