package vn.mavn.patientservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mavn.patientservice.dto.MedicalRecordEditForEmpClinicDto;
import vn.mavn.patientservice.dto.PatientAddDto;
import vn.mavn.patientservice.dto.PatientEditDto;
import vn.mavn.patientservice.dto.qobject.QueryPatientDto;
import vn.mavn.patientservice.entity.Patient;

public interface PatientService {

  Patient addNew(PatientAddDto patientAddDto);

  Patient editPatient(PatientEditDto patientEditDto);

  Patient getById(Long id);

  Page<Patient> findAll(QueryPatientDto queryPatientDto, Pageable pageable);

  void delete(Long id);

}
