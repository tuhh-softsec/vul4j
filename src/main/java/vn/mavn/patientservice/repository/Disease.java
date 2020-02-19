package vn.mavn.patientservice.repository;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.mavn.patientservice.entity.BaseIdEntity;

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
}
