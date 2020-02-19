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
public class DoctorEditDto {

  @NotNull(message = "err.edit.doctor.id-is-mandatory")
  private Long id;
  @NotBlank(message = "err.add.doctor.name-is-mandatory")
  private String name;
  @NotBlank(message = "err.add.doctor.phone-is-mandatory")
  private String phone;
  private String address;
  private String description;
}
