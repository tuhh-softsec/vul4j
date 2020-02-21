package vn.mavn.patientservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordDto {

  private Long id;
  private String userCode;
  private PatientDto patientDto;
  private DiseaseDto diseaseDto;
  private AdvertisingSourceDto advertisingSourceDto;
  private ClinicDto clinicDto;
  private LocalDateTime advisoryDate;
  private String diseaseStatus;
  private String note;
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


  @Setter
  @Getter
  @Builder
  public static class PatientDto{

    private Long id;
    private String name;
    private String address;
  }

  @Setter
  @Getter
  @Builder
  public static class AdvertisingSourceDto{

    private Long id;
    private String name;
  }
}
