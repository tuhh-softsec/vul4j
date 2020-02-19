package vn.mavn.patientservice.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
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
@Entity
@Table(name = "pm_patient")
public class Patient extends BaseIdEntity {

  private String name;
  private Integer age;
  private String address;
  private String phone;
  private String zaLoPhone;
  private String otherPhone;


}
