/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.jdbc;

import java.sql.SQLException;
import java.sql.SQLWarning;

public final class MyDatabaseLogger {

  private MyDatabaseLogger() {}

  public static void logSQLException(final SQLException exception) {
    SQLException ex = exception;
    while (ex != null) {
      System.err.println("------ SQLException ------");
      System.err.println("SQLState: " + ex.getSQLState());
      System.err.println("Vendor Error code: " + ex.getErrorCode());
      System.err.println("Message: " + ex.getMessage());
      System.err.println("Cause: " + ex.getCause().getMessage());
      logSuppressedExceptions(ex);
      ex = ex.getNextException();
    }
  }

  public static void logSuppressedExceptions(final SQLException ex) {
    Throwable[] suppressed = ex.getSuppressed();
    for (Throwable t : suppressed) {
      System.err.println("Suppressed exception: " + t);
    }
  }

  public static void logSQLWarning(final SQLWarning warning) {
    SQLWarning warn = warning;
    while (warn != null) {
      System.err.println("------ SQLWarning ------");
      System.err.println("SQLState: " + warn.getSQLState());
      System.err.println("Vendor Warning code: " + warn.getErrorCode());
      System.err.println("Message: " + warn.getMessage());
      System.err.println("Cause: " + warn.getCause().getMessage());
      warn = warn.getNextWarning();
    }
  }
}
