package vn.mavn.patientservice.service.impl;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.mavn.patientservice.dto.AdvertisingSourceAddDto;
import vn.mavn.patientservice.entity.AdvertisingSource;
import vn.mavn.patientservice.exception.ConflictException;
import vn.mavn.patientservice.repository.AdvertisingSourceRepository;
import vn.mavn.patientservice.service.AdvertisingSourceService;

@Service
public class AdvertisingSourceServiceImpl implements AdvertisingSourceService {

  @Autowired
  private AdvertisingSourceRepository advertisingSourceRepository;

  @Override
  public AdvertisingSource addNew(AdvertisingSourceAddDto advertisingSourceAddDto) {
    //TODO: valid name duplicate
    advertisingSourceRepository.findByName(advertisingSourceAddDto.getName().trim().toUpperCase())
        .ifPresent(adver -> {
          throw new ConflictException(Collections.singletonList("err-advertising-duplicate-name"));
        });
    AdvertisingSource advertisingSource = AdvertisingSource.builder()
        .description(advertisingSourceAddDto.getDescription())
        .name(advertisingSourceAddDto.getName().trim()).build();
    advertisingSource.setCreatedBy(advertisingSourceAddDto.getCreatedBy());
    advertisingSource.setUpdatedBy(advertisingSourceAddDto.getCreatedBy());
    return advertisingSourceRepository.save(advertisingSource);
  }
}
