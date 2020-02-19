package vn.mavn.patientservice.controller;

import io.swagger.annotations.Api;
import java.util.Collections;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.mavn.patientservice.dto.DoctorAddDto;
import vn.mavn.patientservice.dto.DoctorEditDto;
import vn.mavn.patientservice.entity.Doctor;
import vn.mavn.patientservice.response.HttpResponse;
import vn.mavn.patientservice.service.DoctorService;
import vn.mavn.patientservice.service.ResponseService;
import vn.mavn.patientservice.util.EntityValidationUtils;

@RestController
@RequestMapping("/api/v1/doctor")
@Api(tags = "Doctor")
public class DoctorController {

  @Autowired
  private DoctorService doctorService;

  @Autowired
  private ResponseService responseService;

  @PostMapping
  public HttpResponse addNewCustomer(@Valid @RequestBody DoctorAddDto data,
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

}
