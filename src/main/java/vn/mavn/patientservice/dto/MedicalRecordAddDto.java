package vn.mavn.patientservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

  @NotNull(message = "err-medical-record-patient-id-is-mandatory")
  private Long patientId;
  @NotNull(message = "err-medical-record-user-id-is-mandatory")
  private Long userId;
  @NotNull(message = "err-medical-record-user-code-is-mandatory")
  private String userCode;
  @NotNull(message = "err-medical-record-advisory-date-is-mandatory")
  private LocalDateTime advisoryDate;
  @NotNull(message = "err-medical-record-disease-id-is-mandatory")
  private Long diseaseId;
  @NotNull(message = "err-medical-record-advertising-source-id-is-mandatory")
  private Long advertisingSourceId;
  @NotNull(message = "err-medical-record-disease-status-is-mandatory")
  private String diseaseStatus;
  @NotNull(message = "err-medical-record-advisory-status-code-is-mandatory")
  private String advisoryStatusCode;
  private String note;
  @NotNull(message = "err-medical-record-patient-clinic-id-is-mandatory")
  private Long clinicId;
  private LocalDateTime examinationDate;
  private Long examinationTimes;
  private Long remedyAmount;
  private String remedyType;
  private String remedies;
  private BigDecimal totalAmount;
  private BigDecimal transferAmount;
  private BigDecimal codAmount;
  private String extraNote;
  private Boolean isActive;
  @NotNull(message = "err-medical-record-patient-created-by-is-mandatory")
  private Long createdBy;
  private List<MedicineDto> medicineDtos;

  @Setter
  @Getter
  public static class MedicineDto {

    private Long medicineId;
    private Long qty;
  }


}
