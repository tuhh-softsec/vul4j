package vn.mavn.patientservice.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.mavn.patientservice.dto.DoctorAddDto;
import vn.mavn.patientservice.dto.DoctorEditDto;
import vn.mavn.patientservice.entity.Doctor;
import vn.mavn.patientservice.exception.ConflictException;
import vn.mavn.patientservice.exception.NotFoundException;
import vn.mavn.patientservice.repository.DoctorRepository;
import vn.mavn.patientservice.service.DoctorService;

@Service
public class DoctorServiceImpl implements DoctorService {

  @Autowired
  private DoctorRepository doctorRepository;

  @Override
  public Doctor save(DoctorAddDto data) {

    Doctor doctor = new Doctor();

    validationNameOrPhoneWhenAddDoctor(data);
    BeanUtils.copyProperties(data, doctor);
    return doctorRepository.save(doctor);
  }

  private void validationNameOrPhoneWhenAddDoctor(DoctorAddDto data) {
    List<String> failReasons = new ArrayList<>();
    doctorRepository.findByName(data.getName()).ifPresent(doctor1 -> {
      failReasons.add("err.doctor.name-is-duplicate");
    });

    doctorRepository.findByPhone(data.getPhone()).ifPresent(doctor1 -> {
      failReasons.add("err.doctor.phone-is-duplicate");
    });
    if (!CollectionUtils.isEmpty(failReasons)) {
      throw new ConflictException(failReasons);
    }
  }

  @Override
  public Doctor update(DoctorEditDto data) {
    Doctor doctor = doctorRepository.findById(data.getId()).orElseThrow(
        () -> new NotFoundException(Collections.singletonList("err.doctor.doctor-does-not-exist")));

    validationNameOrPhoneWhenEditDoctor(data);
    BeanUtils.copyProperties(data, doctor);
    return doctorRepository.save(doctor);
  }

  private void validationNameOrPhoneWhenEditDoctor(DoctorEditDto data) {
    List<String> failReasons = new ArrayList<>();
    doctorRepository.findByNameAndIdNot(data.getName(), data.getId()).ifPresent(doctor -> {
      failReasons.add("err.doctor.name-is-duplicate");
    });

    doctorRepository.findByPhoneAndIdNot(data.getPhone(), data.getId()).ifPresent(doctor -> {
      failReasons.add("err.doctor.phone-is-duplicate");
    });
    if (!CollectionUtils.isEmpty(failReasons)) {
      throw new ConflictException(failReasons);
    }
  }
}
