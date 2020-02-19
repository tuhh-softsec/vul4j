package vn.mavn.patientservice.service;

import vn.mavn.patientservice.dto.DoctorAddDto;
import vn.mavn.patientservice.dto.DoctorEditDto;
import vn.mavn.patientservice.entity.Doctor;

public interface DoctorService {

  Doctor save(DoctorAddDto data);

  Doctor update(DoctorEditDto data);
}
