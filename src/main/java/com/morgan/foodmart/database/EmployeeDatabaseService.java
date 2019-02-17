package com.morgan.foodmart.database;

import java.util.List;

public interface EmployeeDatabaseService {
  List<String> getDepartments();

  List<String> getPayTypes();

  List<String> getEducation();

  List<EmployeeDetails> getEmployeeDetails(String department, String payType, String education);
}
