package vn.mavn.patientservice.controller.employee;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import vn.mavn.patientservice.dto.ClinicBranchDto;
import vn.mavn.patientservice.response.ResponseWithPage;
import vn.mavn.patientservice.service.ClinicBranchService;

@RestController
@RequestMapping("api/v1/emp/clinic-branches")
@Api(tags = "Counsellor Clinic Branch Controller")
public class CounsellorClinicBranchController {

  @Autowired
  private ClinicBranchService clinicBranchService;

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
    return ResponseEntity.ok(ResponseWithPage.<ClinicBranchDto>builder()
        .data(page.getContent())
        .pageIndex(page.getNumber())
        .totalPage(page.getTotalPages())
        .totalElement(page.getTotalElements()).build());
  }
}
