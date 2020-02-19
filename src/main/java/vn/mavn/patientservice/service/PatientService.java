package vn.mavn.patientservice.service;

import vn.mavn.patientservice.dto.PatientAddDto;
import vn.mavn.patientservice.dto.PatientEditDto;
import vn.mavn.patientservice.entity.Patient;

public interface PatientService {

  Patient addNew(PatientAddDto patientAddDto);

  Patient editPatient(PatientEditDto patientEditDto);

  Patient getById(Long id);
}
