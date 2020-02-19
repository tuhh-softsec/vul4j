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
public class PatientAddDto {

  @NotBlank(message = "err-patient-name-is-mandatory")
  private String name;
  @NotBlank(message = "err-patient-age-is-mandatory")
  private Integer age;
  @NotBlank(message = "err-patient-address-is-mandatory")
  private String address;
  private String phone;
  private String zaLoPhone;
  private String otherPhone;

}
