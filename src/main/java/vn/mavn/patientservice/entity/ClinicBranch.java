package vn.mavn.patientservice.entity;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.mavn.patientservice.entity.base.BaseIdEntity;
import vn.mavn.patientservice.entity.listener.EntityListener;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "pm_clinic_branch")
@EntityListeners(EntityListener.class)
public class ClinicBranch extends BaseIdEntity {

  private String name;
  private Boolean isActive;

}
