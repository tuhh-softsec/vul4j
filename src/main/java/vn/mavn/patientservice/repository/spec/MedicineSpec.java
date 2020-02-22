package vn.mavn.patientservice.repository.spec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import vn.mavn.patientservice.dto.qobject.QueryMedicineDto;
import vn.mavn.patientservice.entity.Medicine;
import vn.mavn.patientservice.util.SpecUtils;

public class MedicineSpec {

  public static Specification<Medicine> findAllMedicines(QueryMedicineDto data,
      List<Long> medicineIds) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      Collection<Predicate> predicates = new ArrayList<>();
      if (StringUtils.isNotBlank(data.getName())) {
        predicates
            .add(SpecUtils.handleAccentExp(criteriaBuilder, root, "name", data.getName()));
      }
      if (!CollectionUtils.isEmpty(medicineIds)) {
        predicates.add(root.get("id").in(medicineIds));
      }
      if (data.getIsActive() != null) {
        predicates.add(criteriaBuilder.equal(root.get("isActive"), data.getIsActive()));
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

}
