package com.morgan.foodmart.database;

import java.sql.SQLException;
import java.util.List;

public interface DatabaseService {
  List<String> getDepartments() throws SQLException;

  List<String> getPayTypes() throws SQLException;

  List<String> getEducation() throws SQLException;

  List<EmployeeDetails> getEmployeeDetails(String department, String payType, String education);
}
