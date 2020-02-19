package vn.mavn.patientservice.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
  @Pattern(regexp = "^(\\+84|0)((2[0-9]{9})|((3|5|7|8|9){1}([0-9]{8})))$",
      message = "err.add.doctor.phoneNum-invalid")
  private String phone;
  private String address;
  private String description;
  private String updatedBy;
}
