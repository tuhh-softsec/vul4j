package vn.mavn.patientservice.controller.employee;

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
import vn.mavn.patientservice.dto.MedicalRecordAddForEmpClinicDto;
import vn.mavn.patientservice.dto.MedicalRecordEditForEmpClinicDto;
import vn.mavn.patientservice.entity.MedicalRecord;
import vn.mavn.patientservice.entity.Patient;
import vn.mavn.patientservice.response.HttpResponse;
import vn.mavn.patientservice.service.MedicalRecordService;
import vn.mavn.patientservice.service.ResponseService;
import vn.mavn.patientservice.util.EntityValidationUtils;

@RestController
@RequestMapping("api/v1/emp-clinic/medical-records")
@Api(tags = "Employee Clinic Medical Record Controller")
public class EmployeeClinicRecordController {

  @Autowired
  private MedicalRecordService medicalRecordService;

  @Autowired
  private ResponseService responseService;



  @PostMapping
  public HttpResponse add(@Valid @RequestBody MedicalRecordAddForEmpClinicDto medicalRecordAddDto,
      BindingResult bindingResult) {
    EntityValidationUtils.processBindingResults(bindingResult);
    MedicalRecord medicalRecord = medicalRecordService.addForEmpClinic(medicalRecordAddDto);
    return responseService.buildUpdatedResponse(medicalRecord.getId(),
        Collections.singletonList("info-medical-record-add-successfully"));
  }

  @PutMapping
  public HttpResponse updatePatientAndMedicalRecordForCounselor(
      @Valid @RequestBody MedicalRecordEditForEmpClinicDto data,
      BindingResult bindingResult) {
    EntityValidationUtils.processBindingResults(bindingResult);
    MedicalRecord medicalRecord = medicalRecordService.editForEmpClinic(data);
    return responseService.buildUpdatedResponse(medicalRecord.getId(),
        Collections.singletonList("info.diseases.update-disease-successfully"));
  }

}
