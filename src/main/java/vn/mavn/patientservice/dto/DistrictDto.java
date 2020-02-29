package vn.mavn.patientservice.dto;

import java.io.Serializable;
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
public class DistrictDto implements Serializable {

  private Long id;
  private String name;
  private String type;
  private ProvinceInfoDto provinceInfoDto;

  @Setter
  @Getter
  @Builder
  public static class ProvinceInfoDto {

    private Long id;
    private String name;
    private String type;

  }


}
