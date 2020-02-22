package vn.mavn.patientservice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.mavn.patientservice.entity.listener.EntityListener;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pm_patient")
@EntityListeners(EntityListener.class)
public class Patient extends BaseIdEntity {

  private String name;
  private Integer age;
  private String address;
  private String phone;
  @Column(name = "zalo_phone")
  private String zaLoPhone;
  private String otherPhone;
  private Boolean isActive;


}
