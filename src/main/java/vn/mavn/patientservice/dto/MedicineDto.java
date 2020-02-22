package vn.mavn.patientservice.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicineDto implements Serializable {

  private Long id;
  private String name;
  private String description;
  private Boolean isActive;
  private Long createdBy;
  private Long updatedBy;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private List<DiseaseDto> diseases;
}
