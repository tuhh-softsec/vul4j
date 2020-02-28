package vn.mavn.patientservice.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineEditDto {

  @NotNull(message = "err.medicines.medicine-id-is-mandatory")
  private Long id;
  @NotBlank(message = "err.medicines.medicine-name-is-mandatory")
  private String name;
  private List<Long> diseaseIds;
  private String description;
  private Boolean isActive;
}