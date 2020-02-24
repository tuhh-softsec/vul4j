package vn.mavn.patientservice.dto.qobject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QueryMedicalRecordDto {

  private String name;
  private Boolean isActive;
  private Long clinicId;
  private String userCode;
  private Long diseaseId;
  private Long advertisingSourceId;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime startDate;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime endDate;
  private Integer patientAge;
  private String phoneNumber;
  private String consultingStatusCode;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime examinationStartDate;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime examinationEndDate;
  private Long examinationTimes;
  private BigDecimal totalAmount;
}
