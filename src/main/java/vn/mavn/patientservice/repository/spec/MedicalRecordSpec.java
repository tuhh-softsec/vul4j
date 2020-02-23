package vn.mavn.patientservice.repository.spec;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import vn.mavn.patientservice.dto.qobject.QueryMedicalRecordDto;
import vn.mavn.patientservice.entity.MedicalRecord;
import vn.mavn.patientservice.util.SpecUtils;

public class MedicalRecordSpec {

  public static Specification<MedicalRecord> findAllMedicines(QueryMedicalRecordDto data) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      Collection<Predicate> predicates = new ArrayList<>();
      if (StringUtils.isNotBlank(data.getName())) {
        predicates
            .add(SpecUtils.handleAccentExp(criteriaBuilder, root, "name", data.getName()));
      }
      if (data.getIsActive() != null) {
        predicates.add(criteriaBuilder.equal(root.get("isActive"), data.getIsActive()));
      }
      predicates = equalLongValueFilter(data.getClinicId(), "clinicId", criteriaBuilder, root,
          predicates);
      predicates = equalLongValueFilter(data.getAdvertisingSourceId(), "advertisingSourceId",
          criteriaBuilder, root, predicates);
      predicates = equalLongValueFilter(data.getDiseaseId(), "diseaseId", criteriaBuilder, root,
          predicates);
      predicates = equalLongValueFilter(data.getPatientId(), "patientId", criteriaBuilder, root,
          predicates);
      if (StringUtils.isNotBlank(data.getUserCode())) {
        predicates.add(criteriaBuilder.equal(root.get("userCode"), data.getUserCode()));
      }
      if (data.getStartDate() != null) {
        Predicate predicate = criteriaBuilder
            .greaterThan(root.get("advisoryDate"), data.getStartDate());
        predicates.add(predicate);
      }

      if (data.getEndDate() != null) {
        Predicate predicate = criteriaBuilder.lessThan(root.get("advisoryDate"), data.getEndDate());
        predicates.add(predicate);
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
