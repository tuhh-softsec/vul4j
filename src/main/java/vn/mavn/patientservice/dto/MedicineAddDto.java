package vn.mavn.patientservice.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineAddDto {

  @NotBlank(message = "err.medicines.medicine-name-is-mandatory")
  private String name;
  @NotNull(message = "err.medicines.disease-id-is-mandatory")
  private Long diseaseId;
  private String description;
  private Boolean isActive;
}