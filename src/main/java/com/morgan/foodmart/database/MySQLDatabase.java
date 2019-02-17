package com.morgan.foodmart.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MySQLDatabase implements DatabaseService {

  private final Connection connection;

  @Autowired
  public MySQLDatabase(Connection connection) {
    this.connection = connection;
  }

  @Override
  public List<String> getDepartments() {
    return executeQuerySingleStringColumn(
        "SELECT department_description FROM foodmart.department GROUP BY department_description");
  }

  @Override
  public List<String> getPayTypes() {
    return executeQuerySingleStringColumn(
        "SELECT pay_type FROM foodmart.`position` GROUP BY pay_type");
  }

  @Override
  public List<String> getEducation() {
    return executeQuerySingleStringColumn(
        "SELECT education_level FROM foodmart.employee GROUP BY education_level");
  }

  @PreDestroy
  public void close() throws SQLException {
    this.connection.close();
  }

  private List<String> executeQuerySingleStringColumn(final String query) {
    ResultSet rs = null;
    Statement stmt = null;
    try {
      stmt = this.connection.createStatement();
      rs = stmt.executeQuery(query);

      List<String> stringColumn = new ArrayList<>();
      while (rs.next()) {
        stringColumn.add(rs.getString(1));
      }

      return stringColumn;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
        }
      }
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException e) {
        }
      }
    }
  }
}
