package vn.mavn.patientservice.dto.qobject;

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
  private Long patientId;
  private Long diseaseId;
  private Long advertisingSourceId;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime startDate;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime endDate;

}
