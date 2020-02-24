package vn.mavn.patientservice.repository.spec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import vn.mavn.patientservice.dto.qobject.QueryMedicalRecordDto;
import vn.mavn.patientservice.entity.MedicalRecord;

public class MedicalRecordSpec {

  public static Specification<MedicalRecord> findAllMedicines(QueryMedicalRecordDto data,
      List<Long> patientIds) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      Collection<Predicate> predicates = new ArrayList<>();
      predicates = equalLongValueFilter(data.getClinicId(), "clinicId", criteriaBuilder, root,
          predicates);
      predicates = equalLongValueFilter(data.getAdvertisingSourceId(), "advertisingSourceId",
          criteriaBuilder, root, predicates);
      predicates = equalLongValueFilter(data.getDiseaseId(), "diseaseId", criteriaBuilder, root,
          predicates);
      if (StringUtils.isNotBlank(data.getUserCode())) {
        predicates.add(criteriaBuilder.equal(root.get("userCode"), data.getUserCode()));
      }

      // Filter by advisory date range
      if (data.getStartDate() != null) {
        Predicate predicate = criteriaBuilder
            .greaterThan(root.get("advisoryDate"), data.getStartDate());
        predicates.add(predicate);
      }
      if (data.getEndDate() != null) {
        Predicate predicate = criteriaBuilder.lessThan(root.get("advisoryDate"), data.getEndDate());
        predicates.add(predicate);
      }

      // Filter by examination date range
      if (data.getExaminationStartDate() != null) {
        predicates.add(criteriaBuilder
            .greaterThan(root.get("examinationDate"), data.getExaminationStartDate()));
      }
      if (data.getExaminationEndDate() != null) {
        predicates.add(criteriaBuilder
            .lessThan(root.get("examinationDate"), data.getExaminationEndDate()));
      }

      // Filter by patients
      if (!CollectionUtils.isEmpty(patientIds)) {
        predicates.add(root.get("patientId").in(patientIds));
      }

      // Filter by total amount range
      if (data.getTotalAmount() != null) {
        predicates
            .add(criteriaBuilder.lessThanOrEqualTo(root.get("totalAmount"), data.getTotalAmount()));
        predicates.add(criteriaBuilder.greaterThan(root.get("totalAmount"), data.getTotalAmount()));
      }

      if (StringUtils.isNotBlank(data.getConsultingStatusCode())) {
        predicates.add(criteriaBuilder
            .equal(root.get("consultingStatusCode"), data.getConsultingStatusCode()));
      }

      if (data.getExaminationTimes() != null) {
        predicates
            .add(criteriaBuilder.equal(root.get("examinationTimes"), data.getExaminationTimes()));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  private static Collection<Predicate> equalLongValueFilter(Long parameter, String fieldName,
      CriteriaBuilder criteriaBuilder, Root root, Collection<Predicate> predicates) {
    if (parameter != null) {
      predicates.add(criteriaBuilder.equal(root.get(fieldName), parameter));
    }
    return predicates;
  }

}
