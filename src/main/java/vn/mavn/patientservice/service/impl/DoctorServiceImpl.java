package vn.mavn.patientservice.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.mavn.patientservice.dto.DoctorAddDto;
import vn.mavn.patientservice.dto.DoctorEditDto;
import vn.mavn.patientservice.entity.Doctor;
import vn.mavn.patientservice.exception.ConflictException;
import vn.mavn.patientservice.exception.NotFoundException;
import vn.mavn.patientservice.repository.ClinicRepository;
import vn.mavn.patientservice.repository.DoctorRepository;
import vn.mavn.patientservice.repository.spec.DoctorSpec;
import vn.mavn.patientservice.service.DoctorService;

@Service
public class DoctorServiceImpl implements DoctorService {

  @Autowired
  private DoctorRepository doctorRepository;

  @Autowired
  private ClinicRepository clinicRepository;

  @Override
  public Doctor save(DoctorAddDto data) {

    Doctor doctor = new Doctor();
    validationNameOrPhoneWhenAddDoctor(data);
    BeanUtils.copyProperties(data, doctor);
    doctor.setName(data.getName());
    return doctorRepository.save(doctor);
  }

  @Override
  public Doctor update(DoctorEditDto data) {
    Doctor doctor = doctorRepository.findById(data.getId()).orElseThrow(
        () -> new NotFoundException(Collections.singletonList("err.doctor.doctor-does-not-exist")));

    validationNameOrPhoneWhenEditDoctor(data);
    BeanUtils.copyProperties(data, doctor);
    doctor.setName(data.getName().trim());
    return doctorRepository.save(doctor);
  }

  @Override
  public Doctor findById(Long id) {

    return doctorRepository.findById(id).orElseThrow(
        () -> new NotFoundException(Collections.singletonList("err.doctor.doctor-does-not-exist")));
  }

  @Override
  public Page<Doctor> findAllDoctors(String name, String phone, Pageable pageable) {

    return (Page<Doctor>) doctorRepository.findAll(
        DoctorSpec.findAllProfiles(name, phone), pageable);
  }

  //TODO:
  @Override
  public void delete(Long id) {
    Doctor doctor = doctorRepository
        .findById(id).orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err-advertising-not-found")));

    // check doctor using in clinic
    clinicRepository.findById(doctor.getId())
        .ifPresent(clinic -> {
          throw new ConflictException(
              Collections.singletonList("err.doctor.doctor-using-in-clinic"));
        });
    doctorRepository.deleteDoctor(doctor.getId());
  }

  private void validationNameOrPhoneWhenAddDoctor(DoctorAddDto data) {
    List<String> failReasons = new ArrayList<>();
    doctorRepository.findByName(data.getName().trim()).ifPresent(doctor -> {
      failReasons.add("err.doctor.name-is-duplicate");
    });

    doctorRepository.findByPhone(data.getPhone()).ifPresent(doctor -> {
      failReasons.add("err.doctor.phone-is-duplicate");
    });
    if (!CollectionUtils.isEmpty(failReasons)) {
      throw new ConflictException(failReasons);
    }
  }

  private void validationNameOrPhoneWhenEditDoctor(DoctorEditDto data) {
    List<String> failReasons = new ArrayList<>();
    doctorRepository.findByNameAndIdNot(data.getName().trim(), data.getId()).ifPresent(doctor -> {
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
