package vn.mavn.patientservice.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import java.util.Collections;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import vn.mavn.patientservice.dto.MedicalRecordAddDto;
import vn.mavn.patientservice.dto.MedicalRecordDto;
import vn.mavn.patientservice.dto.qobject.QueryMedicalRecordDto;
import vn.mavn.patientservice.entity.MedicalRecord;
import vn.mavn.patientservice.response.HttpResponse;
import vn.mavn.patientservice.response.ResponseWithPage;
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
    MedicalRecord medicalRecord = medicalRecordService.addForEmp(medicalRecordAddDto);
    return responseService.buildUpdatedResponse(medicalRecord.getId(),
        Collections.singletonList("info-medical-record-add-successfully"));
  }

  @GetMapping("{id}")
  public ResponseEntity<MedicalRecordDto> getDetailById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(medicalRecordService.getById(id));
  }

  @ApiImplicitParams({
      @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
          value = "Results page you want to retrieve (0..N)", defaultValue = "0"),
      @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
          value = "Number of records per page.", defaultValue = "15"),
      @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string",
          paramType = "query", value = "Sorting criteria in the format: property(,asc|desc). "
          + "Default sort order is ascending. Multiple sort criteria are supported.",
          defaultValue = "createdAt,desc")})
  @GetMapping
  public ResponseEntity<ResponseWithPage<MedicalRecordDto>> all(
      @Valid @ModelAttribute QueryMedicalRecordDto queryMedicalRecordDto,
      BindingResult bindingResult,
      @ApiIgnore Pageable pageable) {
    EntityValidationUtils.processBindingResults(bindingResult);
    Page<MedicalRecordDto> page = medicalRecordService.findAll(queryMedicalRecordDto, pageable);
    return ResponseEntity
        .ok(ResponseWithPage.<MedicalRecordDto>builder().data(page.getContent())
            .pageIndex(page.getNumber())
            .totalPage(page.getTotalPages()).totalElement(page.getTotalElements()).build());
  }

}
