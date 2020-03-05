package vn.mavn.patientservice.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
public class ClinicBranchEditDto {

  @NotNull(message = "err.add.clinic-branch.id-is-mandatory")
  private Long id;
  @NotBlank(message = "err.add.clinic-branch.name-is-mandatory")
  private String name;

}
