package vn.mavn.patientservice.dto.qobject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QueryMedicalRecordDto {

  private String name;
  private Boolean isActive;
  private Long clinicId;
  private String userCode;

}
