package vn.mavn.patientservice.util;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class SpecUtils {

  public static Predicate handleAccentExp(CriteriaBuilder criteriaBuilder, Root root,
      String fieldName, String parameter) {
    parameter = "%" + parameter.toLowerCase().trim() + "%";
    Expression<String> unaccentField = criteriaBuilder
        .function("unaccent", String.class, criteriaBuilder.lower(root.get(fieldName)));
    Expression<String> unaccentParameter = criteriaBuilder
        .function("unaccent", String.class, criteriaBuilder.literal(parameter));
    return criteriaBuilder.like(unaccentField, unaccentParameter);
  }

}
