
package vn.mavn.patientservice.entity.listener;

import java.time.LocalDateTime;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import vn.mavn.patientservice.entity.BaseIdEntity;

/**
 * Created by TaiND on 2020-02-16.
 **/
public class EntityListener {

  @PrePersist
  public void prepareBeforeCreate(BaseIdEntity baseEntity) {
    baseEntity.setCreatedAt(LocalDateTime.now());
    baseEntity.setUpdatedAt(LocalDateTime.now());
  }

  @PreUpdate
  public void prepareBeforeUpdate(BaseIdEntity baseEntity) {
    baseEntity.setUpdatedAt(LocalDateTime.now());
  }
}
