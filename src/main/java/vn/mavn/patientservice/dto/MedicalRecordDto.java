package vn.mavn.patientservice.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.mavn.patientservice.entity.Province;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordDto implements Serializable {

  private Long id;
  private String userCode;
  private PatientDto patientDto;
  private DiseaseForMedicalRecordDto diseaseDto;
  private AdvertisingSourceDto advertisingSourceDto;
  private ClinicDto clinicDto;
  private ConsultingStatusDto consultingStatusDto;
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
  private ClinicBranchDto clinicBranchDto;


  @Setter
  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PatientDto implements Serializable {

    private Long id;
    private String name;
    private String address;
    private Integer age;
    private String phone;
    private String zaloPhone;
    private String otherPhone;
    private Boolean isActive;
    private Province province;
  }

  @Setter
  @Getter
  @Builder
  public static class AdvertisingSourceDto implements Serializable {

    private Long id;
    private String name;
  }

  @Setter
  @Getter
  @Builder
  public static class ConsultingStatusDto implements Serializable {

    private Long id;
    private String name;
    private String code;
  }

  @Setter
  @Getter
  @Builder
  public static class DiseaseForMedicalRecordDto implements Serializable {

    private Long id;
    private String name;
    private List<MedicineDto> medicines;
  }

  @Setter
  @Getter
  @Builder
  public static class MedicineDto implements Serializable {

    private Long id;
    private String name;
    private Integer qty;
  }

  @Setter
  @Getter
  @Builder
  public static class ClinicBranchDto implements Serializable {

    private Long id;
    private String name;
  }


}
