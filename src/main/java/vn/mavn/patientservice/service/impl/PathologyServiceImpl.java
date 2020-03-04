package vn.mavn.patientservice.service.impl;

import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.mavn.patientservice.dto.PathologyAddDto;
import vn.mavn.patientservice.dto.PathologyEditDto;
import vn.mavn.patientservice.entity.Pathology;
import vn.mavn.patientservice.exception.ConflictException;
import vn.mavn.patientservice.exception.NotFoundException;
import vn.mavn.patientservice.repository.PathologyRepository;
import vn.mavn.patientservice.repository.spec.PathologySpec;
import vn.mavn.patientservice.service.PathologyService;
import vn.mavn.patientservice.util.TokenUtils;

@Service
public class PathologyServiceImpl implements PathologyService {

  @Autowired
  private HttpServletRequest httpServletRequest;

  @Autowired
  private PathologyRepository pathologyRepository;

  @Override
  public Page<Pathology> getAllPathologies(String name, Pageable pageable) {
    return pathologyRepository.findAll(PathologySpec.findAllPathologies(name), pageable);
  }

  @Override
  @Transactional
  public Pathology add(PathologyAddDto data) {
    pathologyRepository.findByName(data.getName()).ifPresent(p -> {
      throw new ConflictException(
          Collections.singletonList("err.pathologies.pathology-already-exists"));
    });
    Pathology pathology = new Pathology();
    BeanUtils.copyProperties(data, pathology);
    pathology.setIsActive(true);
    Long userId = Long.parseLong(TokenUtils.getUserIdFromToken(httpServletRequest));
    pathology.setCreatedBy(userId);
    pathology.setUpdatedBy(userId);
    pathologyRepository.save(pathology);
    return pathology;
  }

  @Override
  @Transactional
  public Pathology update(PathologyEditDto data) {
    Pathology pathology = pathologyRepository.findById(data.getId())
        .orElseThrow(() -> new NotFoundException(
            Collections.singletonList("err.pathologies.pathology-not-found")));
    pathologyRepository.findByName(data.getName()).ifPresent(p -> {
      if (!p.getId().equals(pathology.getId())) {
        throw new ConflictException(
            Collections.singletonList("err.pathologies.pathology-already-exists"));
      }
    });
    BeanUtils.copyProperties(data, pathology);
    Long userId = Long.parseLong(TokenUtils.getUserIdFromToken(httpServletRequest));
    pathology.setUpdatedBy(userId);
    pathology.setIsActive(true);
    pathologyRepository.save(pathology);
    return pathology;
  }
}
