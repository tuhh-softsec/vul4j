package vn.mavn.patientservice.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pm_medicine")
@Builder
@Entity
public class Medicine extends BaseIdEntity {

  private String name;
  private Long diseaseId;
  private String description;
  private Boolean isActive;
}
