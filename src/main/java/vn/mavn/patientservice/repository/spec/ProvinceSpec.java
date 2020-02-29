package vn.mavn.patientservice.repository.spec;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import vn.mavn.patientservice.dto.qobject.QueryProvinceDto;
import vn.mavn.patientservice.entity.Disease;
import vn.mavn.patientservice.entity.Province;
import vn.mavn.patientservice.util.SpecUtils;

public class ProvinceSpec {

  public static Specification<Province> findAllProvinces(QueryProvinceDto data) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      Collection<Predicate> predicates = new ArrayList<>();

      if (StringUtils.isNotBlank(data.getName())) {
        Predicate unaccentName = SpecUtils
            .handleAccentExp(criteriaBuilder, root, "name", data.getName());
        predicates.add(unaccentName);
      }

      if (StringUtils.isNotBlank(data.getType())) {
        Predicate unaccentType = SpecUtils
            .handleAccentExp(criteriaBuilder, root, "type", data.getType());
        predicates.add(unaccentType);

      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

}
