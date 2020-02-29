package vn.mavn.patientservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mavn.patientservice.dto.ProvinceDto;
import vn.mavn.patientservice.dto.qobject.QueryProvinceDto;

public interface ProvinceService {

  Page<ProvinceDto> getAllProvinces(QueryProvinceDto queryProvinceDto, Pageable pageable);

  ProvinceDto getById(Long id);
}
