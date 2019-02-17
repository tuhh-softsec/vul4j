package com.morgan.foodmart;

import com.morgan.foodmart.database.DatabaseService;
import com.morgan.foodmart.database.EmployeeDetails;
import com.morgan.foodmart.input.InputChoices;
import com.morgan.foodmart.input.UserInputService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FoodmartApplication implements CommandLineRunner {

  @Autowired private UserInputService userInputService;

  @Autowired private DatabaseService databaseService;

  @Bean
  public Connection getConnection(
      @Value("${mysql.hostname}") String hostname,
      @Value("${mysql.username}") String username,
      @Value("${mysql.password}") String password,
      @Value("${mysql.database}") String database)
      throws SQLException {
    final String connectionString = String.format("jdbc:mysql://%s:3306/%s", hostname, database);
    return DriverManager.getConnection(connectionString, username, password);
  }

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(FoodmartApplication.class);
    app.setLogStartupInfo(false);
    app.setBannerMode(Banner.Mode.OFF);
    app.run(args);
  }

  @Override
  public void run(String... args) {
    InputChoices choices = userInputService.getUserInputChoices();

    List<EmployeeDetails> employeeDetailsList =
        databaseService.getEmployeeDetails(
            choices.getDepartment(), choices.getPayType(), choices.getEducation());

    System.out.println("hello world!");
  }
}
