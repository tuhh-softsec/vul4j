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
public class ProvinceDto implements Serializable {

  private Long id;
  private String name;
  private String type;
  private List<DistrictDto> districtDtos;


  @Setter
  @Getter
  @Builder
  public static class DistrictDto {

    private Long id;
    private String name;
    private String type;
  }

}
