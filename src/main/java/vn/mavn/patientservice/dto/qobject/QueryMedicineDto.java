package vn.mavn.patientservice.dto.qobject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryMedicineDto {

  private Long diseaseId;
  private String name;
  private Boolean isActive;
}
