package vn.mavn.patientservice.dto;

import java.io.Serializable;
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
public class AdvertisingSourceAddDto implements Serializable {

  @NotBlank(message = "err-advertising-name-is-mandatory")
  private String name;
  private String description;
  @NotNull(message = "err-advertising-created-by-is-mandatory")
  private Long createdBy;

}