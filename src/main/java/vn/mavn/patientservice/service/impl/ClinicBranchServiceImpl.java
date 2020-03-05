package vn.mavn.patientservice.service.impl;

import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.mavn.patientservice.dto.ClinicBranchAddDto;
import vn.mavn.patientservice.dto.ClinicBranchDto;
import vn.mavn.patientservice.dto.ClinicBranchEditDto;
import vn.mavn.patientservice.entity.ClinicBranch;
import vn.mavn.patientservice.exception.ConflictException;
import vn.mavn.patientservice.exception.NotFoundException;
import vn.mavn.patientservice.repository.ClinicBranchRepository;
import vn.mavn.patientservice.repository.spec.ClinicBrandSpec;
import vn.mavn.patientservice.service.ClinicBranchService;
import vn.mavn.patientservice.util.TokenUtils;

@Service
public class ClinicBranchServiceImpl implements ClinicBranchService {

  @Autowired
  private ClinicBranchRepository clinicBranchRepository;

  @Autowired
  private HttpServletRequest httpServletRequest;

  @Override
  public ClinicBranch save(ClinicBranchAddDto data) {

    //validate name
    clinicBranchRepository.findByName(data.getName().trim()).ifPresent(clinicBranch -> {
      throw new ConflictException(Collections.singletonList("err.clinic-branch.name-is-duplicate"));
    });

    ClinicBranch clinicBranch = new ClinicBranch();
    BeanUtils.copyProperties(data, clinicBranch);
    clinicBranch.setName(data.getName().trim());
    //Get user logged in ID
    Long loggedInUserId = Long.valueOf(TokenUtils.getUserIdFromToken(httpServletRequest));
    clinicBranch.setCreatedBy(loggedInUserId);
    clinicBranch.setUpdatedBy(loggedInUserId);
    clinicBranch.setIsActive(true);

    return clinicBranchRepository.save(clinicBranch);
  }

  @Override
  public ClinicBranch update(ClinicBranchEditDto data) {

    //check is exist clinic branch
    ClinicBranch clinicBranchOld = clinicBranchRepository.findById(data.getId())
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err.clinic-branch.clinic-branch-does-not-exist")));
    //validation name
    clinicBranchRepository.findByNameAndIdNot(data.getName().trim(), data.getId()).ifPresent(cl -> {
      throw new ConflictException(Collections.singletonList("err.clinic-branch.name-is-duplicate"));
    });

    BeanUtils.copyProperties(data, clinicBranchOld);
    clinicBranchOld.setName(data.getName().trim());

    //Get user logged in ID
    clinicBranchOld.setIsActive(true);
    Long loggedInUserId = Long.valueOf(TokenUtils.getUserIdFromToken(httpServletRequest));
    clinicBranchOld.setUpdatedBy(loggedInUserId);
    clinicBranchRepository.save(clinicBranchOld);

    return clinicBranchOld;
  }

  @Override
  public ClinicBranchDto findById(Long id) {
    //check is exist clinic branch
    ClinicBranch clinicBranchOld = clinicBranchRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err.clinic-branch.clinic-branch-does-not-exist")));

    ClinicBranchDto clinicBranchDto = new ClinicBranchDto();
    BeanUtils.copyProperties(clinicBranchOld, clinicBranchDto);
    return clinicBranchDto;
  }

  @Override
  public Page<ClinicBranchDto> findAllClinics(String name, Pageable pageable) {

    Page<ClinicBranch> clinicBranchPage = clinicBranchRepository.findAll(
        ClinicBrandSpec.findAllClinicBranch(name), pageable);
    if (CollectionUtils.isEmpty(clinicBranchPage.getContent())) {
      return Page.empty(pageable);
    }
    return clinicBranchPage.map(
        clinicBranch -> ClinicBranchDto.builder().id(clinicBranch.getId())
            .name(clinicBranch.getName()).isActive(clinicBranch.getIsActive()).build()
    );
  }

  @Override
  public void delete(Long id) {

    //check is exist clinic branch
    ClinicBranch clinicBranchOld = clinicBranchRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err.clinic-branch.clinic-branch-does-not-exist")));
    clinicBranchRepository.delete(clinicBranchOld);

  }
}
