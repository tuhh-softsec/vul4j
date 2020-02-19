package vn.mavn.patientservice.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiseaseAddDto {

  @NotBlank(message = "err.diseases.disease-name-is-mandatory")
  private String name;
  private String description;
  private Boolean isActive;

}
