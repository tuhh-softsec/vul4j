package vn.mavn.patientservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass /*default for super class*/
@Getter
@Setter
public class BaseEntity implements Serializable {

  @Column(name = "created_at")
  /*use for mark properties java with column in database
  when variable not match with column in database*/
  private LocalDateTime createdAt;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty(access = Access.READ_ONLY)
  private Long id;
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
  @Column(name = "created_by")
  private Long createdBy;
  @Column(name = "updated_by")
  private Long updatedBy;
  @Column(name = "is_active")
  private Boolean isActive;
  @Column(name = "is_deleted")
  private Boolean isDeleted;

}
