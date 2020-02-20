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
import vn.mavn.patientservice.dto.DoctorAddDto;
import vn.mavn.patientservice.dto.DoctorDto;
import vn.mavn.patientservice.dto.DoctorEditDto;
import vn.mavn.patientservice.entity.Doctor;
import vn.mavn.patientservice.response.HttpResponse;
import vn.mavn.patientservice.response.ResponseWithPage;
import vn.mavn.patientservice.service.DoctorService;
import vn.mavn.patientservice.service.ResponseService;
import vn.mavn.patientservice.util.EntityValidationUtils;

@RestController
@RequestMapping("/api/v1/cms/doctors")
@Api(tags = "Doctor")
public class DoctorController {

  @Autowired
  private DoctorService doctorService;

  @Autowired
  private ResponseService responseService;

  @PostMapping
  public HttpResponse addDoctor(@Valid @RequestBody DoctorAddDto data,
      BindingResult bindingResult) {

    EntityValidationUtils.processBindingResults(bindingResult);
    Doctor doctor = doctorService.save(data);
    return responseService.buildUpdatedResponse(doctor.getId(),
        Collections.singletonList("info.doctor.add-doctor-successfully"));
  }

  @PutMapping
  public HttpResponse editDoctor(@Valid @RequestBody DoctorEditDto data,
      BindingResult bindingResult) {

    EntityValidationUtils.processBindingResults(bindingResult);
    Doctor doctor = doctorService.update(data);
    return responseService.buildUpdatedResponse(doctor.getId(),
        Collections.singletonList("info.doctor.edit-doctor-successfully"));
  }

  @GetMapping("{id}")
  public ResponseEntity<DoctorDto> getDoctorById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(doctorService.findById(id));
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
  public ResponseEntity<ResponseWithPage<Doctor>> getAll(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String phone,
      @RequestParam(required = false) Boolean isActive,
      @ApiIgnore Pageable pageable) {
    Page<Doctor> page = doctorService.findAllDoctors(name, phone, isActive, pageable);
    return ResponseEntity
        .ok(ResponseWithPage.<Doctor>builder().data(page.getContent())
            .pageIndex(page.getNumber())
            .totalPage(page.getTotalPages()).totalElement(page.getTotalElements()).build());
  }

  @DeleteMapping
  public HttpResponse remove(@RequestParam Long id) {
    doctorService.delete(id);
    return responseService.buildUpdatedResponse(id,
        Collections.singletonList("info-advertising-delete-successfully"));
  }

}
