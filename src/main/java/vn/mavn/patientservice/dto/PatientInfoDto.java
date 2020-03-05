package vn.mavn.patientservice.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.mavn.patientservice.entity.Province;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientInfoDto implements Serializable {

  private Long id;
  private String name;
  private Integer age;
  private String address;
  private String phone;
  private String zaloPhone;
  private String otherPhone;
  private Boolean isActive;
  private Province province;
  private LocalDateTime updatedAt;
  private LocalDateTime createdAt;
  private List<PatientPathologyDto> pathologies;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class PatientPathologyDto implements Serializable {

    private Long id;
    private String name;
  }

}
