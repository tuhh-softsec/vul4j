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
import vn.mavn.patientservice.dto.PatientAddDto;
import vn.mavn.patientservice.dto.PatientEditDto;
import vn.mavn.patientservice.dto.qobject.QueryPatientDto;
import vn.mavn.patientservice.entity.Patient;
import vn.mavn.patientservice.response.HttpResponse;
import vn.mavn.patientservice.response.ResponseWithPage;
import vn.mavn.patientservice.service.PatientService;
import vn.mavn.patientservice.service.ResponseService;
import vn.mavn.patientservice.util.EntityValidationUtils;

@RestController
@RequestMapping("api/v1/admin/patients")
@Api(tags = "Admin Patient Controller")
public class PatientController {

  @Autowired
  private PatientService patientService;

  @Autowired
  private ResponseService responseService;

  @PostMapping
  public HttpResponse add(@Valid @RequestBody PatientAddDto patientAddDto,
      BindingResult bindingResult) {
    EntityValidationUtils.processBindingResults(bindingResult);
    Patient patient = patientService.addNew(patientAddDto);
    return responseService.buildUpdatedResponse(patient.getId(),
        Collections.singletonList("info-patient-add-successfully"));
  }

  @PutMapping
  public HttpResponse edit(@Valid @RequestBody PatientEditDto patientEditDto,
      BindingResult bindingResult) {
    EntityValidationUtils.processBindingResults(bindingResult);
    Patient patient = patientService
        .editPatient(patientEditDto);

    return responseService.buildUpdatedResponse(patient.getId(),
        Collections.singletonList("info-patient-edit-successfully"));
  }

  @GetMapping("{id}")
  public ResponseEntity<Patient> getDetailById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(patientService.getById(id));
  }

  @DeleteMapping
  public HttpResponse remove(@RequestParam Long id) {
    patientService.delete(id);
    return responseService.buildUpdatedResponse(id,
        Collections.singletonList("info-patient-delete-successfully"));
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
  public ResponseEntity<ResponseWithPage<Patient>> all(
      @Valid @ModelAttribute QueryPatientDto queryPatientDto, BindingResult bindingResult,
      @ApiIgnore Pageable pageable) {
    EntityValidationUtils.processBindingResults(bindingResult);
    Page<Patient> page = patientService.findAll(queryPatientDto, pageable);
    return ResponseEntity
        .ok(ResponseWithPage.<Patient>builder().data(page.getContent())
            .pageIndex(page.getNumber())
            .totalPage(page.getTotalPages()).totalElement(page.getTotalElements()).build());
  }
}
