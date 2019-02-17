package com.morgan.foodmart.input;

import java.util.Objects;

public class InputChoices {
  private final String department;
  private final String payType;
  private final String education;

  public InputChoices(String department, String payType, String education) {
    this.department = department;
    this.payType = payType;
    this.education = education;
  }

  public String getDepartment() {
    return department;
  }

  public String getPayType() {
    return payType;
  }

  public String getEducation() {
    return education;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InputChoices that = (InputChoices) o;
    return Objects.equals(department, that.department)
        && Objects.equals(payType, that.payType)
        && Objects.equals(education, that.education);
  }

  @Override
  public int hashCode() {
    return Objects.hash(department, payType, education);
  }
}
