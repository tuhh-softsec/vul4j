package vn.mavn.patientservice.controller.employee;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import vn.mavn.patientservice.dto.ClinicDto;
import vn.mavn.patientservice.dto.qobject.QueryClinicDto;
import vn.mavn.patientservice.response.ResponseWithPage;
import vn.mavn.patientservice.service.ClinicService;

@RestController
@RequestMapping("api/v1/emp/clinics")
@Api(tags = "Employee Clinic Controller")
public class EmployeeClinicController {

  @Autowired
  private ClinicService clinicService;

  @GetMapping
  @ApiImplicitParams({
      @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
          value = "Results page you want to retrieve (0..N)", defaultValue = "0"),
      @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
          value = "Number of records per page.", defaultValue = "15"),
      @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string",
          paramType = "query", value = "Sorting criteria in the format: property(,asc|desc). "
          + "Default sort order is ascending. Multiple sort criteria are supported.",
          defaultValue = "createdAt,desc")})
  public ResponseEntity<ResponseWithPage<ClinicDto>> getAllClinics(
      @ModelAttribute QueryClinicDto data,
      @ApiIgnore Pageable pageable) {
    data.setIsActive(true);
    Page<ClinicDto> result = clinicService.findAllClinics(data, pageable);
    return ResponseEntity.ok(ResponseWithPage.<ClinicDto>builder()
        .data(result.getContent())
        .totalElement(result.getTotalElements())
        .totalPage(result.getTotalPages())
        .pageIndex(result.getNumber())
        .build());
  }
}
