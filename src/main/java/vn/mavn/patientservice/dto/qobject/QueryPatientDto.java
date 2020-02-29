package vn.mavn.patientservice.dto.qobject;

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
public class QueryPatientDto {

  private String name;
  private String phoneNumber;
  private Integer age;
  private Long provinceCode;
}
