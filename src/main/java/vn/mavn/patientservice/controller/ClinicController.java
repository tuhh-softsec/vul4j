package vn.mavn.patientservice.controller;


import io.swagger.annotations.Api;
import java.util.Collections;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.mavn.patientservice.dto.ClinicAddDto;
import vn.mavn.patientservice.dto.ClinicDto;
import vn.mavn.patientservice.dto.ClinicEditDto;
import vn.mavn.patientservice.entity.Clinic;
import vn.mavn.patientservice.response.HttpResponse;
import vn.mavn.patientservice.service.ClinicService;
import vn.mavn.patientservice.service.ResponseService;
import vn.mavn.patientservice.util.EntityValidationUtils;

@RestController
@RequestMapping("/api/v1/cms/clinics")
@Api(tags = "Clinic")
public class ClinicController {

  @Autowired
  private ClinicService clinicService;

  @Autowired
  private ResponseService responseService;

  @PostMapping
  public HttpResponse addClinic(@Valid @RequestBody ClinicAddDto data,
      BindingResult bindingResult) {

    EntityValidationUtils.processBindingResults(bindingResult);
    Clinic clinic = clinicService.save(data);
    return responseService.buildUpdatedResponse(clinic.getId(),
        Collections.singletonList("info.clinic.add-clinic-successfully"));
  }

  @PutMapping
  public HttpResponse editClinic(@Valid @RequestBody ClinicEditDto data,
      BindingResult bindingResult) {

    EntityValidationUtils.processBindingResults(bindingResult);
    Clinic clinic = clinicService.update(data);
    return responseService.buildUpdatedResponse(clinic.getId(),
        Collections.singletonList("info.clinic.edit-clinic-successfully"));
  }

  @GetMapping("{id}")
  public ResponseEntity<ClinicDto> getDoctorById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(clinicService.findById(id));
  }

}
