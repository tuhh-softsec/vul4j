package vn.mavn.patientservice.repository.spec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import vn.mavn.patientservice.dto.qobject.QueryClinicDto;
import vn.mavn.patientservice.entity.Clinic;
import vn.mavn.patientservice.util.SpecUtils;

public class ClinicSpec {

  public static Specification findAllClinic(QueryClinicDto data, List<Long> ids) {
    return (Specification<Clinic>) (root, criteriaQuery, criteriaBuilder) -> {
      Collection<Predicate> predicates = new ArrayList<>();

      if (StringUtils.isNotBlank(data.getPhone())) {
        predicates.add(criteriaBuilder.equal(root.get("phone"), data.getPhone()));
      }

      if (StringUtils.isNotBlank(data.getName())) {
        Predicate unaccent = SpecUtils
            .handleAccentExp(criteriaBuilder, root, "name", data.getName());
        predicates.add(unaccent);
      }

      if (!CollectionUtils.isEmpty(ids)) {
        predicates.add(root.get("id").in(ids));
      }

      if (data.getIsActive() != null) {
        predicates.add(criteriaBuilder.equal(root.get("isActive"), data.getIsActive()));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

}
