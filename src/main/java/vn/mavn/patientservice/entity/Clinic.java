package vn.mavn.patientservice.entity;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.mavn.patientservice.entity.listener.EntityListener;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "pm_clinic")
@EntityListeners(EntityListener.class)
public class Clinic extends BaseIdEntity {

  private Long doctor_id;
  private String name;
  private String phone;
  private String address;
  private String description;
  private Boolean isActive;

}
