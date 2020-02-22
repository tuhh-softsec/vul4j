package vn.mavn.patientservice.dto.qobject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryClinicDto {

  private String name;
  private String phone;
  private Long userId;
  private Boolean isActive;

}
