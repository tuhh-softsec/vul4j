package vn.mavn.patientservice.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import java.util.Collections;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
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

  @GetMapping("{id}")
  public ResponseEntity<AdvertisingSource> getDetailById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(advertisingSourceService.getById(id));
  }

  @DeleteMapping
  public HttpResponse remove(@RequestParam Long id) {
    advertisingSourceService.delete(id);
    return responseService.buildUpdatedResponse(id,
        Collections.singletonList("info-advertising-delete-successfully"));
  }
}
