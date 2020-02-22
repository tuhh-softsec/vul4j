package vn.mavn.patientservice.dto.qobject;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QueryDoctorDto {

  private String name;
  private String phone;
  private Boolean isActive;

}
