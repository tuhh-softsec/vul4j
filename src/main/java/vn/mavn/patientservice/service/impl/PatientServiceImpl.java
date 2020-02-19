package vn.mavn.patientservice.service.impl;

import java.util.Collections;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.mavn.patientservice.dto.PatientAddDto;
import vn.mavn.patientservice.dto.PatientEditDto;
import vn.mavn.patientservice.entity.Patient;
import vn.mavn.patientservice.exception.BadRequestException;
import vn.mavn.patientservice.exception.ConflictException;
import vn.mavn.patientservice.exception.NotFoundException;
import vn.mavn.patientservice.repository.PatientRepository;
import vn.mavn.patientservice.service.PatientService;

@Service
public class PatientServiceImpl implements PatientService {

  @Autowired
  private PatientRepository patientRepository;

  @Override
  public Patient addNew(PatientAddDto patientAddDto) {
    //TODO: valid name and address
    patientRepository
        .findByNameAndAdress(patientAddDto.getName().trim(), patientAddDto.getAddress().trim())
        .ifPresent(patient -> {
          throw new ConflictException(
              Collections.singletonList("err-patient-duplicate-name-address"));
        });
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
    //TODO: valid name and address
    patientRepository
        .findByNameAndAdressNotEqualId(patientEditDto.getName().trim(),
            patientEditDto.getAddress().trim(), patientEditDto.getId())
        .ifPresent(pa -> {
          throw new ConflictException(
              Collections.singletonList("err-patient-duplicate-name-address"));
        });
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
}

