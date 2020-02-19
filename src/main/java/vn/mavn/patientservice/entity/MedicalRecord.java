package vn.mavn.patientservice.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "pm_medical_record")
public class MedicalRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long patientId;
  private String userCode;
  private LocalDateTime advisoryDate;
  private Long diseaseId;
  private Long advertisingSourceId;
  private String diseaseStatus;
  private String advisoryStatusCode;
  private String note;
  private Long clinicId;
  private LocalDateTime examinationDate;
  private Long examinationTimes;
  private Long remedyAmount;
  private String remedyType;
  private String remedies;
  private Long totalAmount;
  private Long transferAmount;
  private Long codAmount;
  private String extraNote;
}
