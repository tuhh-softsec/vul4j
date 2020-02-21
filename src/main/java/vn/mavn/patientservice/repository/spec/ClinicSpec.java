package vn.mavn.patientservice.repository.spec;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import vn.mavn.patientservice.entity.Clinic;
import vn.mavn.patientservice.util.SpecUtils;

public class ClinicSpec {

  public static Specification findAllClinic(String name, String phone, Boolean isActive) {
    return (Specification<Clinic>) (root, criteriaQuery, criteriaBuilder) -> {
      Collection<Predicate> predicates = new ArrayList<>();

      if (StringUtils.isNotBlank(name)) {
        Predicate unaccent = SpecUtils
            .handleAccentExp(criteriaBuilder, root, "name", name);
        predicates.add(unaccent);
      }
      if (isActive != null) {
        predicates.add(criteriaBuilder.equal(root.get("isActive"), isActive));
      }

      if (StringUtils.isNotBlank(phone)) {
        predicates.add(criteriaBuilder.equal(root.get("phone"), phone));
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

}
