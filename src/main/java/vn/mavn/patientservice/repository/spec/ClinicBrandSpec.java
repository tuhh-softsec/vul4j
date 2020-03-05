package vn.mavn.patientservice.repository.spec;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import vn.mavn.patientservice.entity.ClinicBranch;
import vn.mavn.patientservice.util.SpecUtils;

public class ClinicBrandSpec {

  public static Specification<ClinicBranch> findAllClinicBranch(String name) {
    return (Specification<ClinicBranch>) (root, criteriaQuery, criteriaBuilder) -> {
      Collection<Predicate> predicates = new ArrayList<>();

      if (StringUtils.isNotBlank(name)) {
        Predicate unaccent = SpecUtils
            .handleAccentExp(criteriaBuilder, root, "name", name);
        predicates.add(unaccent);
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

}
