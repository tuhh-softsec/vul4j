package vn.mavn.patientservice.repository.spec;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import vn.mavn.patientservice.dto.qobject.QueryAdvertisingSourceDto;
import vn.mavn.patientservice.entity.AdvertisingSource;
import vn.mavn.patientservice.util.SpecUtils;

public class AdvertisingSourceSpec {

  public static Specification findAllAdvert(QueryAdvertisingSourceDto queryAdvertisingSourceDto) {
    return (Specification<AdvertisingSource>) (root, criteriaQuery, criteriaBuilder) -> {
      Collection<Predicate> predicates = new ArrayList<>();
      if (StringUtils.isNotBlank(queryAdvertisingSourceDto.getName())) {
        Predicate unaccent = SpecUtils
            .handleAccentExp(criteriaBuilder, root, "name", queryAdvertisingSourceDto.getName());
        predicates.add(unaccent);
      }
      if (queryAdvertisingSourceDto.getIsActive() != null) {
        predicates.add(
            criteriaBuilder.equal(root.get("isActive"), queryAdvertisingSourceDto.getIsActive()));
      }
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

}
