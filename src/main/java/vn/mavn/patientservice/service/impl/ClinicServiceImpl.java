package vn.mavn.patientservice.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.mavn.patientservice.dto.ClinicAddDto;
import vn.mavn.patientservice.dto.ClinicEditDto;
import vn.mavn.patientservice.entity.Clinic;
import vn.mavn.patientservice.exception.ConflictException;
import vn.mavn.patientservice.exception.NotFoundException;
import vn.mavn.patientservice.repository.ClinicRepository;
import vn.mavn.patientservice.repository.DoctorRepository;
import vn.mavn.patientservice.service.ClinicService;

@Service
public class ClinicServiceImpl implements ClinicService {

  @Autowired
  private ClinicRepository clinicRepository;

  @Autowired
  private DoctorRepository doctorRepository;

  @Override
  public Clinic save(ClinicAddDto data) {

    Clinic clinic = new Clinic();
    //valid name and phone
    validationNameOrPhoneWhenAddClinic(data);
    //valid doctor
    doctorRepository.findById(data.getDoctor_id()).orElseThrow(
        () -> new NotFoundException(Collections.singletonList("err.doctor.doctor-does-not-exist")));
    BeanUtils.copyProperties(data, clinic);
    clinic.setName(data.getName().trim());
    return clinicRepository.save(clinic);
  }

  @Override
  public Clinic update(ClinicEditDto data) {

    Clinic clinic = clinicRepository.findById(data.getId())
        .orElseThrow(
            () -> new NotFoundException(
                Collections.singletonList("err.clinic.clinic-does-not-exist")));
    //valid name and phone
    validationNameOrPhoneWhenEditClinic(data);
    //valid doctor
    doctorRepository.findById(data.getDoctor_id()).orElseThrow(
        () -> new NotFoundException(Collections.singletonList("err.doctor.doctor-does-not-exist")));
    BeanUtils.copyProperties(data, clinic);
    clinic.setName(data.getName().trim());
    return clinicRepository.save(clinic);
  }

  private void validationNameOrPhoneWhenAddClinic(ClinicAddDto data) {
    List<String> failReasons = new ArrayList<>();
    clinicRepository.findByName(data.getName().trim()).ifPresent(doctor1 -> {
      failReasons.add("err.clinic.name-is-duplicate");
    });

    clinicRepository.findByPhone(data.getPhone()).ifPresent(doctor1 -> {
      failReasons.add("err.clinic.phone-is-duplicate");
    });
    if (!CollectionUtils.isEmpty(failReasons)) {
      throw new ConflictException(failReasons);
    }
  }

  private void validationNameOrPhoneWhenEditClinic(ClinicEditDto data) {
    List<String> failReasons = new ArrayList<>();
    clinicRepository.findByNameAndIdNot(data.getName().trim(), data.getId()).ifPresent(doctor -> {
      failReasons.add("err.clinic.name-is-duplicate");
    });

    clinicRepository.findByPhoneAndIdNot(data.getPhone(), data.getId()).ifPresent(doctor -> {
      failReasons.add("err.clinic.phone-is-duplicate");
    });
    if (!CollectionUtils.isEmpty(failReasons)) {
      throw new ConflictException(failReasons);
    }
  }
}
