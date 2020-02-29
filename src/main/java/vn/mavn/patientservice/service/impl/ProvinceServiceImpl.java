package vn.mavn.patientservice.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.mavn.patientservice.dto.ProvinceDto;
import vn.mavn.patientservice.dto.ProvinceDto.DistrictDto;
import vn.mavn.patientservice.dto.qobject.QueryProvinceDto;
import vn.mavn.patientservice.entity.Province;
import vn.mavn.patientservice.repository.DistrictRepository;
import vn.mavn.patientservice.repository.ProvinceRepository;
import vn.mavn.patientservice.repository.spec.ProvinceSpec;
import vn.mavn.patientservice.service.ProvinceService;

@Service
@Transactional
public class ProvinceServiceImpl implements ProvinceService {

  @Autowired
  private ProvinceRepository provinceRepository;
  @Autowired
  private DistrictRepository districtRepository;

  @Override
  public Page<ProvinceDto> getAllProvinces(QueryProvinceDto queryProvinceDto, Pageable pageable) {

    Page<Province> provincePage = provinceRepository
        .findAll(ProvinceSpec.findAllProvinces(queryProvinceDto), pageable);
    return provincePage.map(province -> {
      ProvinceDto provinceDto = new ProvinceDto();
      List<DistrictDto> districtDtos = districtRepository.findAllByProvinceId(province.getId())
          .stream().map(
              district -> DistrictDto.builder().id(district.getId()).name(district.getName())
                  .type(district.getType()).build()).collect(
              Collectors.toList());

      BeanUtils.copyProperties(province, provinceDto);
      provinceDto.setDistrictDtos(districtDtos);
      return provinceDto;
    });
  }
}
