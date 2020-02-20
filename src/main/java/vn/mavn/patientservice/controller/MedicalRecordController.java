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
import vn.mavn.patientservice.dto.MedicalRecordAddDto;
import vn.mavn.patientservice.dto.MedicalRecordEditDto;
import vn.mavn.patientservice.entity.MedicalRecord;
import vn.mavn.patientservice.response.HttpResponse;
import vn.mavn.patientservice.service.MedicalRecordService;
import vn.mavn.patientservice.service.ResponseService;
import vn.mavn.patientservice.util.EntityValidationUtils;

@RestController
@RequestMapping("api/v1/cms/medical-records")
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

  @PutMapping
  public HttpResponse edit(@Valid @RequestBody MedicalRecordEditDto medicalRecordEditDto,
      BindingResult bindingResult) {
    EntityValidationUtils.processBindingResults(bindingResult);
    MedicalRecord medicalRecord = medicalRecordService
        .editMedicalRecord(medicalRecordEditDto);

    return responseService.buildUpdatedResponse(medicalRecord.getId(),
        Collections.singletonList("info-medical-record-edit-successfully"));
  }
//
//  @GetMapping("{id}")
//  public ResponseEntity<Patient> getDetailById(@PathVariable("id") Long id) {
//    return ResponseEntity.ok(medicalRecordService.getById(id));
//  }
//
//  @DeleteMapping
//  public HttpResponse remove(@RequestParam Long id) {
//    medicalRecordService.delete(id);
//    return responseService.buildUpdatedResponse(id,
//        Collections.singletonList("info-patient-delete-successfully"));
//  }
//
//  @ApiImplicitParams({
//      @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
//          value = "Results page you want to retrieve (0..N)", defaultValue = "0"),
//      @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
//          value = "Number of records per page.", defaultValue = "15"),
//      @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string",
//          paramType = "query", value = "Sorting criteria in the format: property(,asc|desc). "
//          + "Default sort order is ascending. Multiple sort criteria are supported.",
//          defaultValue = "createdAt,desc")})
//  @GetMapping
//  public ResponseEntity<ResponseWithPage<Patient>> all(
//      @Valid @ModelAttribute QueryPatientDto queryPatientDto, BindingResult bindingResult,
//      @ApiIgnore Pageable pageable) {
//    EntityValidationUtils.processBindingResults(bindingResult);
//    Page<Patient> page = medicalRecordService.findAll(queryPatientDto, pageable);
//    return ResponseEntity
//        .ok(ResponseWithPage.<Patient>builder().data(page.getContent())
//            .pageIndex(page.getNumber())
//            .totalPage(page.getTotalPages()).totalElement(page.getTotalElements()).build());
//  }

}
