package vn.mavn.patientservice.controller;

import io.swagger.annotations.Api;
import java.util.Collections;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.mavn.patientservice.dto.MedicalRecordAddDto;
import vn.mavn.patientservice.dto.MedicalRecordDto;
import vn.mavn.patientservice.entity.MedicalRecord;
import vn.mavn.patientservice.response.HttpResponse;
import vn.mavn.patientservice.service.MedicalRecordService;
import vn.mavn.patientservice.service.ResponseService;
import vn.mavn.patientservice.util.EntityValidationUtils;

@RestController
@RequestMapping("api/v1/emp/medical-records")
@Api(tags = "Medical Record Controller")
public class MedicalRecordController {

  @Autowired
  private MedicalRecordService medicalRecordService;

  @Autowired
  private ResponseService responseService;

  @PostMapping
  public HttpResponse add(@Valid @RequestBody MedicalRecordAddDto medicalRecordAddDto,
      BindingResult bindingResult) {
    EntityValidationUtils.processBindingResults(bindingResult);
    MedicalRecord medicalRecord = medicalRecordService.addNew(medicalRecordAddDto);
    return responseService.buildUpdatedResponse(medicalRecord.getId(),
        Collections.singletonList("info-medical-record-add-successfully"));
  }

  @GetMapping("{id}")
  public ResponseEntity<MedicalRecordDto> getDetailById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(medicalRecordService.getById(id));
  }

}
