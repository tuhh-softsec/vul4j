package vn.mavn.patientservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

  @NotNull(message = "err-medical-record-advisory-date-is-mandatory")
  private LocalDateTime advisoryDate;
  @NotNull(message = "err-medical-record-disease-id-is-mandatory")
  private Long diseaseId;
  @NotNull(message = "err-medical-record-advertising-source-id-is-mandatory")
  private Long advertisingSourceId;
  @NotNull(message = "err-medical-record-disease-status-is-mandatory")
  private String diseaseStatus;
  @NotNull(message = "err-medical-record-consulting-status-code-is-mandatory")
  private String consultingStatusCode;
  private String note;
  @NotNull(message = "err-medical-record-clinic-id-is-mandatory")
  private Long clinicId;
  @NotNull(message = "err-medical-record-examination-date-is-mandatory")
  private LocalDateTime examinationDate;
  private Long examinationTimes;
  private String remedyType;
  private String remedyAmount;
  private String remedies;
  @NotNull(message = "err-medical-record-total-amount-is-mandatory")
  private BigDecimal totalAmount;
  @NotNull(message = "err-medical-record-transfer-amount-is-mandatory")
  private BigDecimal transferAmount;
  @NotNull(message = "err-medical-record-cod-amount-is-mandatory")
  private BigDecimal codAmount;
  private String extraNote;
  private Boolean isActive;
  private List<MedicineMappingDto> medicineDtos;
  @NotNull(message = "err-medical-record-patient-is-mandatory")
  @Valid
  private PatientAddDto patientAddDto;


}
