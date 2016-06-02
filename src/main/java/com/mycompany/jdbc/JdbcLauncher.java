/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.jdbc;

/**
 * Helper class to execute other classes.
 */
public final class JdbcLauncher {

  private JdbcLauncher() {}

  public static void main(final String[] args) {
    // Always start derby database first in order to connect
    DatabaseParameters parameters =
        new DatabaseParameters.Builder("jdbc:derby://localhost:1527/sample", "app", "app").build();
    MyDatabase db = new MyDatabase(parameters);

    db.submitQueriesAndReadResults();
    db.constructAndUseStatement();
    db.getInformationAboutResultSet();
    db.printReport();
    db.moveAroundResultSets();
    db.demonstrateGetRowCount();
    db.updateResultSet();
    db.getInformationAboutDatabase();
    db.usePreparedStatement();
    db.workWithRowSet();
    db.demonstrateTransaction();
    db.demonstrateTransactionUsingSavepoints();
  }

}
