package vn.mavn.patientservice.dto;

import java.io.Serializable;
import java.util.List;
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
public class DoctorDto {

  private Long id;
  private String name;
  private String phone;
  private String address;
  private String description;
  private List<ClinicDto> clinics;
  private Boolean isActive;

  @Setter
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ClinicDto implements Serializable {

    private Long id;
    private String name;

  }

}
