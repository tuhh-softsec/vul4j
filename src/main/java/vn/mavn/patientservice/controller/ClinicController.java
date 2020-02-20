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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import vn.mavn.patientservice.dto.ClinicAddDto;
import vn.mavn.patientservice.dto.ClinicDto;
import vn.mavn.patientservice.dto.ClinicEditDto;
import vn.mavn.patientservice.entity.Clinic;
import vn.mavn.patientservice.response.HttpResponse;
import vn.mavn.patientservice.response.ResponseWithPage;
import vn.mavn.patientservice.service.ClinicService;
import vn.mavn.patientservice.service.ResponseService;
import vn.mavn.patientservice.util.EntityValidationUtils;

@RestController
@RequestMapping("/api/v1/cms/clinics")
@Api(tags = "Clinic")
public class ClinicController {

  @Autowired
  private ClinicService clinicService;

  @Autowired
  private ResponseService responseService;

  @PostMapping
  public HttpResponse addClinic(@Valid @RequestBody ClinicAddDto data,
      BindingResult bindingResult) {

    EntityValidationUtils.processBindingResults(bindingResult);
    Clinic clinic = clinicService.save(data);
    return responseService.buildUpdatedResponse(clinic.getId(),
        Collections.singletonList("info.clinic.add-clinic-successfully"));
  }

  @PutMapping
  public HttpResponse editClinic(@Valid @RequestBody ClinicEditDto data,
      BindingResult bindingResult) {

    EntityValidationUtils.processBindingResults(bindingResult);
    Clinic clinic = clinicService.update(data);
    return responseService.buildUpdatedResponse(clinic.getId(),
        Collections.singletonList("info.clinic.edit-clinic-successfully"));
  }

  @GetMapping("{id}")
  public ResponseEntity<ClinicDto> getDoctorById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(clinicService.findById(id));
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
  public ResponseEntity<ResponseWithPage<Clinic>> getAll(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String phone,
      @RequestParam(required = false) Boolean isActive,
      @ApiIgnore Pageable pageable) {
    Page<Clinic> page = clinicService.findAllClinics(name, phone, isActive, pageable);
    return ResponseEntity
        .ok(ResponseWithPage.<Clinic>builder().data(page.getContent())
            .pageIndex(page.getNumber())
            .totalPage(page.getTotalPages()).totalElement(page.getTotalElements()).build());
  }

}
