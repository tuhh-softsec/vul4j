package com.morgan.foodmart.transformer;

import com.morgan.foodmart.database.EmployeeDetails;
import java.util.List;

public interface EmployeeTransformer {

  List<Employee> toEmployeeList(List<EmployeeDetails> employeeDetails);
}
