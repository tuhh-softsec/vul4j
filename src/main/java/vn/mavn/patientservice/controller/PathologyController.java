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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import vn.mavn.patientservice.dto.PathologyAddDto;
import vn.mavn.patientservice.dto.PathologyEditDto;
import vn.mavn.patientservice.entity.Pathology;
import vn.mavn.patientservice.response.HttpResponse;
import vn.mavn.patientservice.response.ResponseWithPage;
import vn.mavn.patientservice.service.PathologyService;
import vn.mavn.patientservice.service.ResponseService;
import vn.mavn.patientservice.util.EntityValidationUtils;

@RestController
@RequestMapping("api/v1/admin/pathologies")
@Api(tags = "Admin Pathology Controller")
public class PathologyController {

  @Autowired
  private PathologyService pathologyService;

  @Autowired
  private ResponseService responseService;

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
  public ResponseEntity<ResponseWithPage<Pathology>> getAllPathologies(
      @RequestParam(required = false) String name,
      @ApiIgnore Pageable pageable) {
    Page<Pathology> result = pathologyService.getAllPathologies(name, pageable);
    return ResponseEntity.ok(ResponseWithPage.<Pathology>builder()
        .data(result.getContent())
        .totalElement(result.getTotalElements())
        .totalPage(result.getTotalPages())
        .pageIndex(result.getNumber())
        .build());
  }

  @PostMapping
  public HttpResponse addNewPathology(@Valid @RequestBody PathologyAddDto data,
      BindingResult bindingResult) {
    EntityValidationUtils.processBindingResults(bindingResult);
    Long pathologyId = pathologyService.add(data).getId();
    return responseService.buildUpdatedResponse(pathologyId,
        Collections.singletonList("info.pathologies.add-pathology-successfully"));
  }

  @PutMapping
  public HttpResponse updatePathology(@Valid @RequestBody PathologyEditDto data,
      BindingResult bindingResult) {
    EntityValidationUtils.processBindingResults(bindingResult);
    Long pathologyId = pathologyService.update(data).getId();
    return responseService.buildUpdatedResponse(pathologyId,
        Collections.singletonList("info.pathologies.update-pathology-successfully"));
  }
}
