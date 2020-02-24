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
import vn.mavn.patientservice.dto.MedicineAddDto;
import vn.mavn.patientservice.dto.MedicineDto;
import vn.mavn.patientservice.dto.MedicineEditDto;
import vn.mavn.patientservice.dto.qobject.QueryMedicineDto;
import vn.mavn.patientservice.entity.Medicine;
import vn.mavn.patientservice.response.HttpResponse;
import vn.mavn.patientservice.response.ResponseWithPage;
import vn.mavn.patientservice.service.MedicineService;
import vn.mavn.patientservice.service.ResponseService;
import vn.mavn.patientservice.util.EntityValidationUtils;

@RestController
@RequestMapping("api/v1/admin/medicines")
@Api(tags = "Admin Medicine Controller")
public class MedicineController {

  @Autowired
  private MedicineService medicineService;

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
  public ResponseEntity<ResponseWithPage<MedicineDto>> getAllMedicines(
      @ModelAttribute QueryMedicineDto data, @ApiIgnore Pageable pageable) {
    Page<MedicineDto> result = medicineService.getAllMedicines(data, pageable);
    return ResponseEntity.ok(ResponseWithPage.<MedicineDto>builder()
        .data(result.getContent())
        .totalPage(result.getTotalPages())
        .totalElement(result.getTotalElements())
        .pageIndex(result.getNumber())
        .build());
  }

  @PostMapping
  public HttpResponse addNewMedicine(@Valid @RequestBody MedicineAddDto data,
      BindingResult bindingResult) {
    EntityValidationUtils.processBindingResults(bindingResult);
    Long medicineId = medicineService.add(data).getId();
    return responseService.buildUpdatedResponse(medicineId,
        Collections.singletonList("info.medicines.add-medicine-successfully"));
  }

  @PutMapping
  public HttpResponse update(@Valid @RequestBody MedicineEditDto data,
      BindingResult bindingResult) {
    EntityValidationUtils.processBindingResults(bindingResult);
    Long medicineId = medicineService.update(data).getId();
    return responseService.buildUpdatedResponse(medicineId,
        Collections.singletonList("info.medicines.update-medicine-successfully"));
  }

  @GetMapping("{id}")
  public ResponseEntity<MedicineDto> getDetail(@PathVariable("id") Long id) {
    return ResponseEntity.ok(medicineService.detail(id));
  }

  @DeleteMapping
  public HttpResponse removeMedicine(@RequestParam Long id) {
    medicineService.remove(id);
    return responseService.buildUpdatedResponse(id,
        Collections.singletonList("info.medicines.remove-medicine-successfully"));
  }
}
