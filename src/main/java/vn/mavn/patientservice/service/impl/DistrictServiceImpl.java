package vn.mavn.patientservice.service.impl;

import java.util.Collections;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.mavn.patientservice.dto.DistrictDto;
import vn.mavn.patientservice.dto.DistrictDto.ProvinceInfoDto;
import vn.mavn.patientservice.dto.qobject.QueryDistrictDto;
import vn.mavn.patientservice.entity.District;
import vn.mavn.patientservice.entity.Province;
import vn.mavn.patientservice.exception.NotFoundException;
import vn.mavn.patientservice.repository.DistrictRepository;
import vn.mavn.patientservice.repository.ProvinceRepository;
import vn.mavn.patientservice.repository.spec.DistrictSpec;
import vn.mavn.patientservice.service.DistrictService;

@Service
@Transactional
public class DistrictServiceImpl implements DistrictService {

  @Autowired
  private DistrictRepository districtRepository;
  @Autowired
  private ProvinceRepository provinceRepository;

  @Override
  public Page<DistrictDto> getAllDistricts(QueryDistrictDto queryDistrictDto, Pageable pageable) {
    Page<District> districts = districtRepository
        .findAll(DistrictSpec.findAllDistricts(queryDistrictDto), pageable);
    return districts.map(district -> {
      DistrictDto districtDto = new DistrictDto();
      BeanUtils.copyProperties(district, districtDto);
      Province province = provinceRepository.findById(district.getProvinceId())
          .orElseThrow(() -> new NotFoundException(
              Collections.singletonList("err-province-service-province-not-found")));
      ProvinceInfoDto provinceInfoDto = ProvinceInfoDto.builder().id(province.getId())
          .name(province.getName()).type(province.getType()).build();
      districtDto.setProvinceInfoDto(provinceInfoDto);
      return districtDto;
    });
    
  }

  @Override
  public DistrictDto getById(Long id) {
    return null;
  }
}
