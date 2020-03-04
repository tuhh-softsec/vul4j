package vn.mavn.patientservice.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PathologyEditDto {

  @NotNull(message = "err.pathologies.pathology-id-is-mandatory")
  private Long id;
  @NotBlank(message = "err.pathologies.name-is-mandatory")
  private String name;
  private String description;
}
