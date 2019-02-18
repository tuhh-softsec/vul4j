package com.morgan.foodmart.transformer;

import static org.junit.Assert.assertEquals;

import com.morgan.foodmart.database.EmployeeDetails;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EmployeeTransformerTest {

  private static final EmployeeTransformer employeeTransformer = new EmployeeTransformerImpl();

  @Test
  public void testEmployeeListGeneratedCorrectly() {
    final List<EmployeeDetails> employeeDetails =
        Arrays.asList(
            new EmployeeDetails(
                "test employee 1",
                "software engineer",
                parseDate("01-02-2015"),
                null,
                "head office"),
            new EmployeeDetails(
                "test employee 2",
                "senior software engineer",
                parseDate("04-07-2018"),
                parseDate("01-01-2019"),
                "warehouse"));

    final List<Employee> expectedEmployeeList =
        Arrays.asList(
            new Employee(
                "test employee 1",
                "software engineer",
                "01-02-2015",
                Constants.missingDate,
                "head office"),
            new Employee(
                "test employee 2",
                "senior software engineer",
                "04-07-2018",
                "01-01-2019",
                "warehouse"));

    final List<Employee> employeeList = employeeTransformer.toEmployeeList(employeeDetails);

    assertEquals(expectedEmployeeList, employeeList);
  }

  private Date parseDate(final String date) {
    try {
      return new SimpleDateFormat("dd-MM-yyyy").parse(date);
    } catch (ParseException e) {
      return null;
    }
  }
}
