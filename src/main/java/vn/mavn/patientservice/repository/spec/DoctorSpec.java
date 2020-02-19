package vn.mavn.patientservice.repository.spec;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import vn.mavn.patientservice.entity.AdvertisingSource;
import vn.mavn.patientservice.entity.Doctor;
import vn.mavn.patientservice.util.SpecUtils;

public class DoctorSpec {

  public static Specification findAllProfiles(String name, String phone) {
    return (Specification<Doctor>) (root, criteriaQuery, criteriaBuilder) -> {
      Collection<Predicate> predicates = new ArrayList<>();
      if (StringUtils.isNotBlank(name)) {
        Predicate unaccent = SpecUtils
            .handleAccentExp(criteriaBuilder, root, "name", name);
        predicates.add(unaccent);
      }
      if (StringUtils.isNotBlank(phone)) {
        predicates.add(criteriaBuilder.equal(root.get("phone"), phone));
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

}
