package vn.mavn.patientservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mavn.patientservice.dto.DiseaseAddDto;
import vn.mavn.patientservice.dto.DiseaseEditDto;
import vn.mavn.patientservice.dto.qobject.QueryDiseaseDto;
import vn.mavn.patientservice.entity.Disease;

public interface DiseaseService {

  Page<Disease> getAllDisease(QueryDiseaseDto data, Pageable pageable);

  Disease add(DiseaseAddDto data);

  Disease update(DiseaseEditDto data);

  Disease detail(Long id);

  void removeDisease(Long id);
}
