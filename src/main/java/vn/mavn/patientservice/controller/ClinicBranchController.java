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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import vn.mavn.patientservice.dto.ClinicBranchAddDto;
import vn.mavn.patientservice.dto.ClinicBranchDto;
import vn.mavn.patientservice.dto.ClinicBranchEditDto;
import vn.mavn.patientservice.entity.ClinicBranch;
import vn.mavn.patientservice.response.HttpResponse;
import vn.mavn.patientservice.response.ResponseWithPage;
import vn.mavn.patientservice.service.ClinicBranchService;
import vn.mavn.patientservice.service.ResponseService;
import vn.mavn.patientservice.util.EntityValidationUtils;

@RestController
@RequestMapping("/api/v1/admin/clinic-branch")
@Api(tags = "Admin Clinic Branch")
public class ClinicBranchController {

  @Autowired
  private ClinicBranchService clinicBranchService;

  @Autowired
  private ResponseService responseService;

  @PostMapping
  public HttpResponse addClinicBranch(@Valid @RequestBody ClinicBranchAddDto data,
      BindingResult bindingResult) {

    EntityValidationUtils.processBindingResults(bindingResult);
    ClinicBranch clinicBranch = clinicBranchService.save(data);
    return responseService.buildUpdatedResponse(clinicBranch.getId(),
        Collections.singletonList("info.clinic.add-clinic-branch-successfully"));
  }

  @PutMapping
  public HttpResponse editClinicBranch(@Valid @RequestBody ClinicBranchEditDto data,
      BindingResult bindingResult) {

    EntityValidationUtils.processBindingResults(bindingResult);
    ClinicBranch clinicBranch = clinicBranchService.update(data);
    return responseService.buildUpdatedResponse(clinicBranch.getId(),
        Collections.singletonList("info.clinic.edit-clinic-branch-successfully"));
  }

  @GetMapping("{id}")
  public ResponseEntity<ClinicBranchDto> getClinicBranchById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(clinicBranchService.findById(id));
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
  public ResponseEntity<ResponseWithPage<ClinicBranchDto>> getAll(
      @RequestParam(required = false) String name, @ApiIgnore Pageable pageable) {
    Page<ClinicBranchDto> page = clinicBranchService.findAllClinics(name, pageable);
    return ResponseEntity
        .ok(ResponseWithPage.<ClinicBranchDto>builder().data(page.getContent())
            .pageIndex(page.getNumber())
            .totalPage(page.getTotalPages()).totalElement(page.getTotalElements()).build());
  }

  @DeleteMapping
  public HttpResponse remove(@RequestParam Long id) {
    clinicBranchService.delete(id);
    return responseService.buildUpdatedResponse(id,
        Collections.singletonList("info.clinic.delete-clinic-branch-successfully"));
  }

}
