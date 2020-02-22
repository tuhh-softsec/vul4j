package vn.mavn.patientservice.dto;

import java.util.List;
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
public class ClinicEditDto {

  @NotNull(message = "err.edit.clinic-is-mandatory")
  private Long id;
  @NotBlank(message = "err.add.clinic.name-is-mandatory")
  private String name;
  @NotBlank(message = "err.add.clinic.phone-is-mandatory")
  @Pattern(regexp = "^(\\+84|0)((2[0-9]{9})|((3|5|7|8|9){1}([0-9]{8})))$",
      message = "err.add.clinic.phoneNum-invalid")
  private String phone;
  private String address;
  private String description;
  private Long doctorId;
  private Boolean isActive;
  @NotNull(message = "err.add.clinic.diseaseIds-is-mandatory")
  private List<Long> diseaseIds;
  private List<Long> userIds;
}
