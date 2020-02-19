package vn.mavn.patientservice.service.impl;

import java.util.Collections;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.mavn.patientservice.dto.DiseaseAddDto;
import vn.mavn.patientservice.entity.Disease;
import vn.mavn.patientservice.exception.ConflictException;
import vn.mavn.patientservice.repository.DiseaseRepository;
import vn.mavn.patientservice.repository.spec.DiseaseSpec;
import vn.mavn.patientservice.dto.DiseaseEditDto;
import vn.mavn.patientservice.service.DiseaseService;

@Service
public class DiseaseServiceImpl implements DiseaseService {

  @Autowired
  private DiseaseRepository diseaseRepository;

  @Override
  public Page<Disease> getAllDisease(String name, Pageable pageable) {
    if (StringUtils.isBlank(name)) {
      return diseaseRepository.findAll(pageable);
    } else {
      return diseaseRepository.findAll(DiseaseSpec.findAllDiseases(name), pageable);
    }
  }

  @Override
  public Disease add(DiseaseAddDto data) {
    diseaseRepository.findByName(data.getName()).ifPresent(disease -> {
      throw new ConflictException(
          Collections.singletonList("err.diseases.disease-already-exists"));
    });
    Disease disease = Disease.builder().name(data.getName()).description(data.getDescription())
        .build();
    diseaseRepository.save(disease);
    return disease;
  }

  @Override
  public Disease update(DiseaseEditDto data) {
    Disease disease = diseaseRepository.findById(data.getId())
        .orElseThrow(() -> new ConflictException(
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
    return diseaseRepository.findById(id).orElseThrow(() -> new ConflictException(
        Collections.singletonList("err.diseases.disease-does-not-exist")));
  }

  @Override
  public void removeDisease(Long id) {
    diseaseRepository.findById(id).orElseThrow(() -> new ConflictException(
        Collections.singletonList("err.diseases.disease-does-not-exist")));
    diseaseRepository.deleteById(id);
  }
}
