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
public class ClinicDto {

  private String name;
  private String phone;
  private String address;
  private String description;
  private DoctorDto doctor;
  private List<DiseaseDto> diseases;

  @Setter
  @Getter
  @Builder
  public static class DoctorDto implements Serializable {

    private Long id;
    private String name;
  }

}
