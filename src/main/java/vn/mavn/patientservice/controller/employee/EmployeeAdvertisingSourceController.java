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
import vn.mavn.patientservice.dto.qobject.QueryAdvertisingSourceDto;
import vn.mavn.patientservice.entity.AdvertisingSource;
import vn.mavn.patientservice.response.ResponseWithPage;
import vn.mavn.patientservice.service.AdvertisingSourceService;

@RestController
@RequestMapping("api/v1/emp/advertising-sources")
@Api(tags = "Employee Advertising Source Controller")
public class EmployeeAdvertisingSourceController {

  @Autowired
  private AdvertisingSourceService advertisingSourceService;

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
  public ResponseEntity<ResponseWithPage<AdvertisingSource>> getAllAdvertisingSources(
      @ModelAttribute QueryAdvertisingSourceDto data, @ApiIgnore Pageable pageable) {
    Page<AdvertisingSource> result = advertisingSourceService.findAll(data, pageable);
    return ResponseEntity.ok(ResponseWithPage.<AdvertisingSource>builder()
        .data(result.getContent())
        .totalElement(result.getTotalElements())
        .totalPage(result.getTotalPages())
        .pageIndex(result.getNumber())
        .build());
  }
}
