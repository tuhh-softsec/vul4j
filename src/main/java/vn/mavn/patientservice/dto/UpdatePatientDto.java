package vn.mavn.patientservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
public class UpdatePatientDto {

  @NotNull
  private Long id;
  @NotBlank
  private String name;

  @NotNull
  private String age;
  @NotBlank
  private String address;
  @NotBlank
  private String phone;
  @NotBlank
  private Long AdvertisingSourceId;
  //tinh trang benh
  @NotBlank
  private String diseaseStatus;
  //tinh trang tu van
  @NotBlank
  private String consultingStatusCode;
  //ghi chu
  private String extraNote;
  private String zaloPhone;
  private String otherPhone;
  //danh sach benh
  @NotEmpty
  private List<Long> diseaseIds;

  //loại thuốc , nhap free
  private String remedyType;
  //so thang mac dinh 0
  private String remedyAmount;

  //danh sach vi thuoc + so luong
  @Valid
  private List<MedicineMappingDto> medicineDtos;
  //bai thuoc
  private String remedies;
  //tong tien mat
  @NotNull(message = "err-medical-record-patient-total-amount-is-mandatory")
  private BigDecimal totalAmount;
  //tien chuyen khoan
  @NotNull(message = "err-medical-record-patient-transfer-amount-is-mandatory")
  private BigDecimal transferAmount;
  //tien COD
  @NotNull(message = "err-medical-record-patient-cod-amount-is-mandatory")
  private BigDecimal codAmount;

  //ngay kham
  @NotNull(message = "err-medical-record-examination-date-is-mandatory")
  private LocalDateTime examinationDate;

  private Boolean isActive;
}
