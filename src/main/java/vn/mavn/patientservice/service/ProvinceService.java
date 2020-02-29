package vn.mavn.patientservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mavn.patientservice.dto.qobject.QueryProvinceDto;
import vn.mavn.patientservice.entity.Province;

public interface ProvinceService {

  Page<Province> getAllProvinces(QueryProvinceDto queryProvinceDto, Pageable pageable);

  Province getById(Long id);
}
