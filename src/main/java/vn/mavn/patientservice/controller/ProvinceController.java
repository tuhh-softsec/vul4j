package vn.mavn.patientservice.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import vn.mavn.patientservice.dto.ProvinceDto;
import vn.mavn.patientservice.dto.qobject.QueryProvinceDto;
import vn.mavn.patientservice.response.ResponseWithPage;
import vn.mavn.patientservice.service.ProvinceService;

@RestController
@RequestMapping("api/v1/admin/provinces")
@Api(tags = "Admin Province Controller")

public class ProvinceController {

  @Autowired
  private ProvinceService provinceService;

  @GetMapping
  @ApiImplicitParams({
      @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
          value = "Results page you want to retrieve (0..N)", defaultValue = "0"),
      @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
          value = "Number of records per page.", defaultValue = "15"),
      @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string",
          paramType = "query", value = "Sorting criteria in the format: property(,asc|desc). "
          + "Default sort order is ascending. Multiple sort criteria are supported.",
          defaultValue = "name,desc")})
  public ResponseEntity<ResponseWithPage<ProvinceDto>> getAllProvinces(
      @ModelAttribute QueryProvinceDto queryProvinceDto, @ApiIgnore Pageable pageable) {
    Page<ProvinceDto> result = provinceService.getAllProvinces(queryProvinceDto, pageable);
    return ResponseEntity.ok(ResponseWithPage.<ProvinceDto>builder()
        .data(result.getContent())
        .totalPage(result.getTotalPages())
        .totalElement(result.getTotalElements())
        .pageIndex(result.getNumber())
        .build());
  }

  @GetMapping("{id}")
  public ResponseEntity<ProvinceDto> getDetailById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(provinceService.getById(id));
  }

}
