package vn.mavn.patientservice.service.impl;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.mavn.patientservice.dto.DiseaseAddDto;
import vn.mavn.patientservice.dto.DiseaseEditDto;
import vn.mavn.patientservice.dto.qobject.QueryDiseaseDto;
import vn.mavn.patientservice.entity.ClinicDisease;
import vn.mavn.patientservice.entity.Disease;
import vn.mavn.patientservice.entity.MedicalRecord;
import vn.mavn.patientservice.exception.ConflictException;
import vn.mavn.patientservice.exception.NotFoundException;
import vn.mavn.patientservice.repository.ClinicDiseaseRepository;
import vn.mavn.patientservice.repository.DiseaseRepository;
import vn.mavn.patientservice.repository.MedicalRecordRepository;
import vn.mavn.patientservice.repository.spec.DiseaseSpec;
import vn.mavn.patientservice.service.DiseaseService;

@Service
public class DiseaseServiceImpl implements DiseaseService {

  @Autowired
  private DiseaseRepository diseaseRepository;

  @Autowired
  private ClinicDiseaseRepository clinicDiseaseRepository;

  @Autowired
  private MedicalRecordRepository medicalRecordRepository;

  @Override
  public Page<Disease> getAllDisease(QueryDiseaseDto data, Pageable pageable) {
    return diseaseRepository.findAll(DiseaseSpec.findAllDiseases(data), pageable);
  }

  @Override
  public Disease add(DiseaseAddDto data) {
    diseaseRepository.findByName(data.getName()).ifPresent(disease -> {
      throw new ConflictException(
          Collections.singletonList("err.diseases.disease-already-exists"));
    });
    Disease disease = new Disease();
    BeanUtils.copyProperties(data, disease);
    diseaseRepository.save(disease);
    return disease;
  }

  @Override
  public Disease update(DiseaseEditDto data) {
    Disease disease = diseaseRepository.findById(data.getId())
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err.diseases.disease-not-found")));
    diseaseRepository.findByName(data.getName()).ifPresent(d -> {
      if (!d.getId().equals(disease.getId())) {
        throw new ConflictException(
            Collections.singletonList("err.diseases.disease-already-exists"));
      }
    });
    BeanUtils.copyProperties(data, disease);
    diseaseRepository.save(disease);
    return disease;
  }

  @Override
  public Disease detail(Long id) {
    return diseaseRepository.findById(id).orElseThrow(() -> new NotFoundException(
        Collections.singletonList("err.diseases.disease-not-found")));
  }

  @Override
  public void removeDisease(Long id) {
    Disease disease = diseaseRepository.findById(id).orElseThrow(() -> new NotFoundException(
        Collections.singletonList("err.diseases.disease-not-found")));
    List<ClinicDisease> clinicDiseases = clinicDiseaseRepository.findByDiseaseId(id);
    List<MedicalRecord> medicalRecords = medicalRecordRepository.findByDiseaseId(id);
    if (!CollectionUtils.isEmpty(clinicDiseases) || !CollectionUtils.isEmpty(medicalRecords)) {
      throw new ConflictException(Collections.singletonList("err.diseases.cannot-remove-disease"));
    }
    disease.setIsActive(false);
    diseaseRepository.save(disease);
  }
}
