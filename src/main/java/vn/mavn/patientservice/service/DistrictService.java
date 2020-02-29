package vn.mavn.patientservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mavn.patientservice.dto.DistrictDto;
import vn.mavn.patientservice.dto.qobject.QueryDistrictDto;
import vn.mavn.patientservice.dto.qobject.QueryProvinceDto;

public interface DistrictService {

  Page<DistrictDto> getAllDistricts(QueryDistrictDto queryDistrictDto, Pageable pageable);

  DistrictDto getById(Long id);
}
