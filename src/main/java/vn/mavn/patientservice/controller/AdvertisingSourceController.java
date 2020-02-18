package vn.mavn.patientservice.controller;

import java.util.Collections;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.mavn.patientservice.dto.AdvertisingSourceAddDto;
import vn.mavn.patientservice.dto.AdvertisingSourceEditDto;
import vn.mavn.patientservice.entity.AdvertisingSource;
import vn.mavn.patientservice.response.HttpResponse;
import vn.mavn.patientservice.service.AdvertisingSourceService;
import vn.mavn.patientservice.service.ResponseService;
import vn.mavn.patientservice.util.EntityValidationUtils;

@RestController
@RequestMapping("api/v1/cms/advertising-sources")
public class AdvertisingSourceController {

  @Autowired
  private AdvertisingSourceService advertisingSourceService;

  @Autowired
  private ResponseService responseService;

  @PostMapping
  public HttpResponse add(@Valid @RequestBody AdvertisingSourceAddDto advertisingSourceAddDto,
      BindingResult bindingResult) {
    EntityValidationUtils.processBindingResults(bindingResult);
    AdvertisingSource advertisingSource = advertisingSourceService.addNew(advertisingSourceAddDto);

    return responseService.buildUpdatedResponse(advertisingSource.getId(),
        Collections.singletonList("info-advertising-add-successfully"));
  }

  @PutMapping
  public HttpResponse edit(@Valid @RequestBody AdvertisingSourceEditDto advertisingSourceEditDto,
      BindingResult bindingResult) {
    EntityValidationUtils.processBindingResults(bindingResult);
    AdvertisingSource advertisingSource = advertisingSourceService.editAdvertSource(advertisingSourceEditDto);

    return responseService.buildUpdatedResponse(advertisingSource.getId(),
        Collections.singletonList("info-advertising-edit-successfully"));
  }
}
