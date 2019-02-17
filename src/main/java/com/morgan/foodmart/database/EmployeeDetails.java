package com.morgan.foodmart.database;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class EmployeeDetails {
  private String fullName;
  private String firstName;
  private String lastName;
  private String positionTitle;
  private Date birthDate;
  private Date hireDate;
  private Date endDate;
  private BigDecimal salary;
  private String martialStatus;
  private String gender;
  private String managementRole;

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getPositionTitle() {
    return positionTitle;
  }

  public void setPositionTitle(String positionTitle) {
    this.positionTitle = positionTitle;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public Date getHireDate() {
    return hireDate;
  }

  public void setHireDate(Date hireDate) {
    this.hireDate = hireDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public BigDecimal getSalary() {
    return salary;
  }

  public void setSalary(BigDecimal salary) {
    this.salary = salary;
  }

  public String getMartialStatus() {
    return martialStatus;
  }

  public void setMartialStatus(String martialStatus) {
    this.martialStatus = martialStatus;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getManagementRole() {
    return managementRole;
  }

  public void setManagementRole(String managementRole) {
    this.managementRole = managementRole;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EmployeeDetails that = (EmployeeDetails) o;
    return Objects.equals(fullName, that.fullName)
        && Objects.equals(firstName, that.firstName)
        && Objects.equals(lastName, that.lastName)
        && Objects.equals(positionTitle, that.positionTitle)
        && Objects.equals(birthDate, that.birthDate)
        && Objects.equals(hireDate, that.hireDate)
        && Objects.equals(endDate, that.endDate)
        && Objects.equals(salary, that.salary)
        && Objects.equals(martialStatus, that.martialStatus)
        && Objects.equals(gender, that.gender)
        && Objects.equals(managementRole, that.managementRole);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        fullName,
        firstName,
        lastName,
        positionTitle,
        birthDate,
        hireDate,
        endDate,
        salary,
        martialStatus,
        gender,
        managementRole);
  }
}
