package vn.mavn.patientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.mavn.patientservice.entity.Medicine;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicineDto extends Medicine {

  private DiseaseDto disease;
}
