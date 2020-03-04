package vn.mavn.patientservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mavn.patientservice.dto.PathologyAddDto;
import vn.mavn.patientservice.dto.PathologyEditDto;
import vn.mavn.patientservice.entity.Pathology;

public interface PathologyService {

  Page<Pathology> getAllPathologies(String name, Pageable pageable);

  Pathology add(PathologyAddDto data);

  Pathology update(PathologyEditDto data);
}
