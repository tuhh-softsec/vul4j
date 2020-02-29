package vn.mavn.patientservice.service.impl;

import java.util.Collections;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.mavn.patientservice.dto.qobject.QueryProvinceDto;
import vn.mavn.patientservice.entity.Province;
import vn.mavn.patientservice.exception.NotFoundException;
import vn.mavn.patientservice.repository.ProvinceRepository;
import vn.mavn.patientservice.repository.spec.ProvinceSpec;
import vn.mavn.patientservice.service.ProvinceService;

@Service
@Transactional
public class ProvinceServiceImpl implements ProvinceService {

  @Autowired
  private ProvinceRepository provinceRepository;

  @Override
  public Page<Province> getAllProvinces(QueryProvinceDto queryProvinceDto, Pageable pageable) {
    return provinceRepository
        .findAll(ProvinceSpec.findAllProvinces(queryProvinceDto), pageable);
  }

  @Override
  public Province getById(Long id) {
    // valid parameter id province
    return provinceRepository.findById(id).orElseThrow(
        () -> new NotFoundException(
            Collections.singletonList("err-province-service-province-not-found"))
    );
  }

}
