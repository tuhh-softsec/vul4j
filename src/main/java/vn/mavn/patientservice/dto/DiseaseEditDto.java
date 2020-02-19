package vn.mavn.patientservice.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiseaseEditDto {

  @NotNull(message = "err.diseases.disease-id-is-mandatory")
  private Long id;
  @NotBlank(message = "err.diseases.disease-name-is-mandatory")
  private String name;
  private String description;
}
