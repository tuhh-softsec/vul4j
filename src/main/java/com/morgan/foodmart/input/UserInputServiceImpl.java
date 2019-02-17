package com.morgan.foodmart.input;

import com.morgan.foodmart.database.DatabaseService;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserInputServiceImpl implements UserInputService {

  private final DatabaseService databaseService;

  @Autowired
  public UserInputServiceImpl(final DatabaseService databaseService) {
    this.databaseService = databaseService;
  }

  @Override
  public InputChoices getUserInputChoices() {
    try {
      final List<String> departments = databaseService.getDepartments();
      final List<String> payTypes = databaseService.getPayTypes();
      final List<String> eduacationLevels = databaseService.getEducation();

      final String department = getChoice(departments, "Choose a department:");
      final String payType = getChoice(payTypes, "Choose a pay type:");
      final String educationLevel = getChoice(eduacationLevels, "Choose an education level:");

      return new InputChoices(department, payType, educationLevel);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private String getChoice(final List<String> choices, final String helpMessage) {

    final int maxNumberChoices = choices.size();
    System.out.println(helpMessage);
    for (int i = 0; i < maxNumberChoices; i++) {
      final String line = String.format("%d) %s", i + 1, choices.get(i));
      System.out.println(line);
    }

    int choice = 0;

    while (choice < 1 || choice > maxNumberChoices) {
      System.out.print(String.format("Your choice [1-%d]: ", maxNumberChoices));

      Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
      choice = scanner.nextInt();

      if (choice > maxNumberChoices) {
        System.out.println("Invalid choice");
      }
    }

    return choices.get(choice - 1);
  }
}
