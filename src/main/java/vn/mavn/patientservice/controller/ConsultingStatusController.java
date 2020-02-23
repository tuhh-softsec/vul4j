package vn.mavn.patientservice.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import vn.mavn.patientservice.entity.ConsultingStatus;
import vn.mavn.patientservice.response.ResponseWithPage;
import vn.mavn.patientservice.service.ConsultingStatusService;

@RestController
@RequestMapping("api/v1/admin/consulting-statuses")
@Api(tags = "Admin Consulting Controller")
public class ConsultingStatusController {

  @Autowired
  private ConsultingStatusService consultingStatusService;

  @GetMapping
  @ApiImplicitParams({
      @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
          value = "Results page you want to retrieve (0..N)", defaultValue = "0"),
      @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
          value = "Number of records per page.", defaultValue = "15"),
      @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string",
          paramType = "query", value = "Sorting criteria in the format: property(,asc|desc). "
          + "Default sort order is ascending. Multiple sort criteria are supported.",
          defaultValue = "sort,desc")})
  public ResponseEntity<ResponseWithPage<ConsultingStatus>> getAllConsultingStatuses(
      @ApiIgnore Pageable pageable) {
    Page<ConsultingStatus> result = consultingStatusService.getAllConsultingStatuses(pageable);
    return ResponseEntity.ok(ResponseWithPage.<ConsultingStatus>builder()
        .data(result.getContent())
        .totalPage(result.getTotalPages())
        .totalElement(result.getTotalElements())
        .pageIndex(result.getNumber())
        .build());
  }


}
