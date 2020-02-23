package vn.mavn.patientservice.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordAddDto {

  @NotNull(message = "err-medical-record-advertising-source-id-is-mandatory")
  private Long advertisingSourceId;
  @NotNull(message = "err-medical-record-disease-status-is-mandatory")
  private String diseaseStatus;
  @NotNull(message = "err-medical-record-consulting-status-code-is-mandatory")
  private String consultingStatusCode;
  private String note;
  @NotNull(message = "err-medical-record-clinic-id-is-mandatory")
  private Long clinicId;
  private String extraNote;
  private Boolean isActive;
  @NotNull(message = "err-medical-record-patient-is-mandatory")
  @Valid
  private PatientAddDto patientAddDto;


}
