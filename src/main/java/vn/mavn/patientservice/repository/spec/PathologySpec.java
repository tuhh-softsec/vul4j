package vn.mavn.patientservice.repository.spec;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import vn.mavn.patientservice.entity.Pathology;
import vn.mavn.patientservice.util.SpecUtils;

public class PathologySpec {

  public static Specification<Pathology> findAllPathologies(String name) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      Collection<Predicate> predicates = new ArrayList<>();
      if (StringUtils.isNotBlank(name)) {
        Predicate unaccentName = SpecUtils
            .handleAccentExp(criteriaBuilder, root, "name", name);
        predicates.add(unaccentName);
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}
