package vn.mavn.patientservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.mavn.patientservice.entity.listener.EntityListener;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pm_medicine")
@Builder
@Entity
@EntityListeners(EntityListener.class)
public class Medicine extends BaseIdEntity {

  private String name;
  private String description;
  private Boolean isActive;
  @JsonIgnore
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "pm_medicine_disease",
      joinColumns = {@JoinColumn(name = "medicine_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "disease_id", referencedColumnName = "id")})
  private List<Disease> diseases;
}
