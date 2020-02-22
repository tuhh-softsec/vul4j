package vn.mavn.patientservice.repository.spec;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import vn.mavn.patientservice.dto.qobject.QueryDoctorDto;
import vn.mavn.patientservice.entity.AdvertisingSource;
import vn.mavn.patientservice.entity.Doctor;
import vn.mavn.patientservice.util.SpecUtils;

public class DoctorSpec {

  public static Specification findAllProfiles(QueryDoctorDto data) {
    return (Specification<Doctor>) (root, criteriaQuery, criteriaBuilder) -> {
      Collection<Predicate> predicates = new ArrayList<>();
      if (StringUtils.isNotBlank(data.getName())) {
        Predicate unaccent = SpecUtils
            .handleAccentExp(criteriaBuilder, root, "name", data.getName());
        predicates.add(unaccent);
      }
      if (StringUtils.isNotBlank(data.getPhone())) {
        predicates.add(criteriaBuilder.equal(root.get("phone"), data.getPhone()));
      }
      if (data.getIsActive() != null) {
        predicates.add(criteriaBuilder.equal(root.get("isActive"), data.getIsActive()));
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

}
