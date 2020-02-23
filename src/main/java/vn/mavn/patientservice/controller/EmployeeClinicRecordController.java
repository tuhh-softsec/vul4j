package vn.mavn.patientservice.controller;

import io.swagger.annotations.Api;
import java.util.Collections;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.mavn.patientservice.dto.MedicalRecordEditForEmpClinicDto;
import vn.mavn.patientservice.entity.Patient;
import vn.mavn.patientservice.response.HttpResponse;
import vn.mavn.patientservice.service.MedicalRecordService;
import vn.mavn.patientservice.service.PatientService;
import vn.mavn.patientservice.service.ResponseService;
import vn.mavn.patientservice.util.EntityValidationUtils;

@RestController
@RequestMapping("/api/v1/emp-clinic/update-medical")
@Api(tags = "Emp clinic Controller")
public class EmployeeClinicRecordController {

  @Autowired
  private ResponseService responseService;

  @Autowired
  private PatientService patientService;

  @Autowired
  private MedicalRecordService medicalRecordService;


  @PutMapping
  public HttpResponse updatePatientAndMedicalRecordForCounselor(
      @Valid @RequestBody MedicalRecordEditForEmpClinicDto data,
      BindingResult bindingResult) {
    EntityValidationUtils.processBindingResults(bindingResult);
    Patient patient = patientService.updatePatientAndMedicalRecordForCounselor(data);
    return responseService.buildUpdatedResponse(patient.getId(),
        Collections.singletonList("info.diseases.update-disease-successfully"));
  }

}
