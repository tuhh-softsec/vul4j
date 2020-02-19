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
import vn.mavn.patientservice.dto.DiseaseAddDto;
import vn.mavn.patientservice.dto.DiseaseEditDto;
import vn.mavn.patientservice.dto.qobject.QueryDiseaseDto;
import vn.mavn.patientservice.entity.Disease;
import vn.mavn.patientservice.response.HttpResponse;
import vn.mavn.patientservice.response.ResponseWithPage;
import vn.mavn.patientservice.service.DiseaseService;
import vn.mavn.patientservice.service.ResponseService;
import vn.mavn.patientservice.util.EntityValidationUtils;

@RestController
@RequestMapping("/api/v1/admin/disease")
@Api(tags = "Admin Disease Controller")
public class DiseaseController {

  @Autowired
  private DiseaseService diseaseService;

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
  public ResponseEntity<ResponseWithPage<Disease>> getAllDiseases(
      @ModelAttribute QueryDiseaseDto data,
      @ApiIgnore Pageable pageable) {
    Page<Disease> result = diseaseService.getAllDisease(data, pageable);
    return ResponseEntity.ok(ResponseWithPage.<Disease>builder()
        .data(result.getContent())
        .totalElement(result.getTotalElements())
        .pageIndex(result.getNumber())
        .totalPage(result.getTotalPages())
        .build());
  }

  @PostMapping
  public HttpResponse addNewDisease(@Valid @RequestBody DiseaseAddDto data,
      BindingResult bindingResult) {
    EntityValidationUtils.processBindingResults(bindingResult);
    Long diseaseId = diseaseService.add(data).getId();
    return responseService.buildUpdatedResponse(diseaseId,
        Collections.singletonList("info.diseases.add-disease-successfully"));
  }

  @PutMapping
  public HttpResponse updateDisease(@Valid @RequestBody DiseaseEditDto data,
      BindingResult bindingResult) {
    EntityValidationUtils.processBindingResults(bindingResult);
    Long diseaseId = diseaseService.update(data).getId();
    return responseService.buildUpdatedResponse(diseaseId,
        Collections.singletonList("info.diseases.update-disease-successfully"));
  }

  @GetMapping("{id}")
  public ResponseEntity<Disease> getDetail(@PathVariable("id") Long id) {
    return ResponseEntity.ok(diseaseService.detail(id));
  }

  @DeleteMapping
  public HttpResponse removeDisease(@RequestParam Long id) {
    diseaseService.removeDisease(id);
    return responseService.buildUpdatedResponse(id,
        Collections.singletonList("info.diseases.remove-disease-successfully"));
  }
}
