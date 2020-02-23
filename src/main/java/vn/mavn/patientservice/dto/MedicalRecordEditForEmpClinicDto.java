package vn.mavn.patientservice.dto;

import java.math.BigDecimal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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
public class MedicalRecordEditForEmpClinicDto {

  @NotNull(message = "err-medical-record-advertising-source-id-is-mandatory")
  private Long advertisingSourceId;
  @NotNull(message = "err-medical-record-disease-id-is-mandatory")
  private List<Long> diseaseIds;
  @NotNull(message = "err-medical-record-disease-status-is-mandatory")
  private String diseaseStatus;
  @NotNull(message = "err-medical-record-consulting-status-code-is-mandatory")
  private String consultingStatusCode;
  private String note;
  @NotNull(message = "err-medical-record-clinic-id-is-mandatory")
  private Long clinicId;
  private String remedyType;
  private Long remedyAmount;
  private String remedies;
  @NotNull(message = "err-medical-record-total-amount-is-mandatory")
  private BigDecimal totalAmount;
  @NotNull(message = "err-medical-record-transfer-amount-is-mandatory")
  private BigDecimal transferAmount;
  @NotNull(message = "err-medical-record-cod-amount-is-mandatory")
  private BigDecimal codAmount;
  private String extraNote;
  private Boolean isActive;
  @NotEmpty(message = "err-medical-record-medicineDtos-is-mandatory")
  private List<MedicineMappingDto> medicineDtos;
  @NotNull(message = "err-medical-record-patient-is-mandatory")
  @Valid
  private PatientEditDto patientEditDto;

}
