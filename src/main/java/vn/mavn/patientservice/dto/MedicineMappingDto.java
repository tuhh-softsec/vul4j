package vn.mavn.patientservice.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MedicineMappingDto implements Serializable {

  private Long medicineId;
  private Long qty;
}
