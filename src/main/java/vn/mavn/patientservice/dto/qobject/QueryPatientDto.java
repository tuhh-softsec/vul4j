package vn.mavn.patientservice.dto.qobject;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QueryPatientDto {

  private String name;
  private Boolean isActive;
}
