package vn.mavn.patientservice.dto.qobject;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryMedicineDto {

  private List<Long> diseaseIds;
  private String name;
  private Boolean isActive;
}
