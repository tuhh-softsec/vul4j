package vn.mavn.patientservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.mavn.patientservice.dto.AdvertisingSourceAddDto;
import vn.mavn.patientservice.dto.AdvertisingSourceEditDto;
import vn.mavn.patientservice.entity.AdvertisingSource;

public interface AdvertisingSourceService {

  AdvertisingSource addNew(AdvertisingSourceAddDto advertisingSourceAddDto);

  AdvertisingSource editAdvertSource(AdvertisingSourceEditDto advertisingSourceEditDto);

  AdvertisingSource getById(Long id);

  void delete(Long id);

  Page<AdvertisingSource> findAll(String name, Pageable pageable);

}
