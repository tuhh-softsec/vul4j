package vn.mavn.patientservice.service;

import vn.mavn.patientservice.dto.AdvertisingSourceAddDto;
import vn.mavn.patientservice.dto.AdvertisingSourceEditDto;
import vn.mavn.patientservice.entity.AdvertisingSource;

public interface AdvertisingSourceService {

  AdvertisingSource addNew(AdvertisingSourceAddDto advertisingSourceAddDto);

  AdvertisingSource editAdvertSource(AdvertisingSourceEditDto advertisingSourceEditDto);

}
