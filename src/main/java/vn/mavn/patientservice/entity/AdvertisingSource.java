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

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pm_advertising_source")
@Builder
@EntityListeners(EntityListener.class)
public class AdvertisingSource extends BaseIdEntity {

  private String name;
  private String description;
  private Boolean isActive;

}
