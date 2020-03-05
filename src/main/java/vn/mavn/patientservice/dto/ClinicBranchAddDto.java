package vn.mavn.patientservice.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicBranchAddDto {

  @NotBlank(message = "err.add.clinic-branch.name-is-mandatory")
  private String name;

}
