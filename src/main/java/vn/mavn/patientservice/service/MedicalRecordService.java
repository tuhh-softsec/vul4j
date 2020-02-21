package vn.mavn.patientservice.service;

import vn.mavn.patientservice.dto.MedicalRecordAddDto;
import vn.mavn.patientservice.dto.MedicalRecordDto;
import vn.mavn.patientservice.entity.MedicalRecord;

public interface MedicalRecordService {

  MedicalRecord addNew(MedicalRecordAddDto medicalRecordAddDto);

  MedicalRecordDto getById(Long id);
}

