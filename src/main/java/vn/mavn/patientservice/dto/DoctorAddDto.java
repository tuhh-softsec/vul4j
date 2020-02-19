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
public class DoctorAddDto {

  @NotBlank(message = "err.add.doctor.name-is-mandatory")
  private String name;
  @NotBlank(message = "err.add.doctor.phone-is-mandatory")
  private String phone;
  private String address;
  private String description;
  private Long createdBy;
}
