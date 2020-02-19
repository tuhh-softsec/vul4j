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
@Table(name = "pm_disease")
@Entity
@Builder
public class Disease extends BaseIdEntity {

  private String name;
  private String description;
  private Boolean isActive;
}
