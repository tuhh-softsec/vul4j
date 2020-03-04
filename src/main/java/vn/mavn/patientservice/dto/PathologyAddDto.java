package vn.mavn.patientservice.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PathologyAddDto {

  @NotBlank(message = "err.pathologies.name-is-mandatory")
  private String name;
  private String description;
}
