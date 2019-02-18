package com.morgan.foodmart.transformer;

import com.morgan.foodmart.database.EmployeeDetails;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class EmployeeTransformerImpl implements EmployeeTransformer {

  private final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

  @Override
  public List<Employee> toEmployeeList(List<EmployeeDetails> employeeDetails) {
    return employeeDetails
        .stream()
        .map(
            e -> {
              final String hireDate = formatDate(e.getHireDate());
              final String endDate = formatDate(e.getEndDate());

              return new Employee(
                  e.getFullName(), e.getPositionTitle(), hireDate, endDate, e.getManagementRole());
            })
        .collect(Collectors.toList());
  }

  private String formatDate(final Date date) {
    if (date != null) {
      return format.format(date);
    }
    return Constants.missingDate;
  }
}
