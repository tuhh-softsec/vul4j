package vn.mavn.patientservice.repository.spec;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import vn.mavn.patientservice.entity.AdvertisingSource;
import vn.mavn.patientservice.util.SpecUtils;

public class AdvertisingSourceSpec {

  public static Specification findAllProfiles(String name) {
    return (Specification<AdvertisingSource>) (root, criteriaQuery, criteriaBuilder) -> {
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
