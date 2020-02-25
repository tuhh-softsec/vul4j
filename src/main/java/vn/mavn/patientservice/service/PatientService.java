package vn.mavn.patientservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mavn.patientservice.dto.PatientDto;
import vn.mavn.patientservice.dto.qobject.QueryPatientDto;
import vn.mavn.patientservice.entity.Patient;

public interface PatientService {

  Patient addNew(PatientDto patientDto);

  Patient editPatient(PatientDto patientEditDto);

  Patient getById(Long id);

  Page<Patient> findAll(QueryPatientDto queryPatientDto, Pageable pageable);

  void delete(Long id);

}
