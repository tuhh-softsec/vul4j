package vn.mavn.patientservice.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineAddDto {

  @NotBlank(message = "err.medicines.medicine-name-is-mandatory")
  private String name;
  @NotEmpty(message = "err.medicines.disease-id-is-mandatory")
  private List<Long> diseaseIds;
  private String description;
  private Boolean isActive;
}