package com.morgan.foodmart.transformer;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class Employee {
  @SerializedName("full_name")
  private final String fullName;

  @SerializedName("position_title")
  private final String positionTitle;

  @SerializedName("hire_date")
  private final String hireDate;

  @SerializedName("end_date")
  private final String endDate;

  @SerializedName("management_role")
  private final String managementRole;

  Employee(
      String fullName,
      String positionTitle,
      String hireDate,
      String endDate,
      String managementRole) {
    this.fullName = fullName;
    this.positionTitle = positionTitle;
    this.hireDate = hireDate;
    this.endDate = endDate;
    this.managementRole = managementRole;
  }

  public String getFullName() {
    return fullName;
  }

  public String getPositionTitle() {
    return positionTitle;
  }

  public String getHireDate() {
    return hireDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public String getManagementRole() {
    return managementRole;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Employee employee = (Employee) o;
    return Objects.equals(fullName, employee.fullName)
        && Objects.equals(positionTitle, employee.positionTitle)
        && Objects.equals(hireDate, employee.hireDate)
        && Objects.equals(endDate, employee.endDate)
        && Objects.equals(managementRole, employee.managementRole);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fullName, positionTitle, hireDate, endDate, managementRole);
  }
}
