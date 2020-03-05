package vn.mavn.patientservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mavn.patientservice.dto.ClinicBranchAddDto;
import vn.mavn.patientservice.dto.ClinicBranchDto;
import vn.mavn.patientservice.dto.ClinicBranchEditDto;
import vn.mavn.patientservice.entity.ClinicBranch;

public interface ClinicBranchService {

  ClinicBranch save(ClinicBranchAddDto data);

  ClinicBranch update(ClinicBranchEditDto data);

  ClinicBranchDto findById(Long id);

  Page<ClinicBranchDto> findAllClinics(String name, Pageable pageable);

  void delete(Long id);
}
