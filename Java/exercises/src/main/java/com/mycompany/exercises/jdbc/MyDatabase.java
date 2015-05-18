/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetProvider;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * From OCA/OCP Java SE7 Programmer 1 & 2 Study Guide, Chapter 15: JDBC.
 */
public class MyDatabase {

    // Local Apache Derby database available in NetBeans.
    private final String url = "jdbc:derby://localhost:1527/sample";
    private final String username = "app";
    private final String password = "app";

    public MyDatabase() {
        createPoolingDriver();
    }

    /**
     * Creates Database Connection Pool using Apache Commons DBCP. This
     * minimizes the number of times you close and re-create Connection objects.
     * Note: DBCP 2 compiles and runs under Java 7 only (JDBC 4.1).
     */
    private void createPoolingDriver() {
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url, username, password);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
        ObjectPool connectionPool = new GenericObjectPool(poolableConnectionFactory);
        poolableConnectionFactory.setPool(connectionPool);
        PoolingDriver driver = new PoolingDriver();
        driver.registerPool("example", connectionPool);
    }

    /**
     * Always start derby database first in order to connect.
     */
    public void submitQueriesAndReadResults() {
        String query = "SELECT * FROM Customer";
        try (Connection conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:example");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            // Process Results
            while (rs.next()) {
                // Print Columns
                System.out.print(rs.getInt("Customer_ID") + " ");
                System.out.print(rs.getString("NAME") + " ");
                System.out.print(rs.getString("EMAIL") + " ");
                System.out.println(rs.getString("PHONE") + " ");
            }
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    /**
     * Always start derby database first in order to connect.
     */
    public void constructAndUseStatement() {
        try (Connection conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:example");
                Statement stmt = conn.createStatement()) {
            ResultSet rs;
            int numRows;
            boolean status = stmt.execute("SELECT * FROM Customer WHERE NAME LIKE 'B%'"); // True if there is a ResulSet
            if (status) {                         // True
                rs = stmt.getResultSet();         // Get the ResulSet
                processResultSet(rs);
            } else {
                numRows = stmt.getUpdateCount();  // Get the update count
                if (numRows == -1) {              // If -1, there are no results            
                    System.out.println("No results");
                } else {                          // else, print the number of
                    // rows affected
                    System.out.println(numRows + " rows affected.");
                }
            }
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    private void processResultSet(final ResultSet rs) throws SQLException {
        while (rs.next()) {
            // Print Columns
            System.out.print(rs.getInt("CUSTOMER_ID") + " ");
            System.out.print(rs.getString("NAME") + " ");
            System.out.print(rs.getString("EMAIL") + " ");
            System.out.println(rs.getString("PHONE") + " ");
        }
    }

    /**
     * Always start derby database first in order to connect.
     */
    public void getInformationAboutResultSet() {
        String query = "SELECT Customer_ID FROM Customer";
        try (Connection conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:example");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData rsmd = rs.getMetaData();
            // How many columns in this ResultSet?
            int colCount = rsmd.getColumnCount();
            System.out.println("Column Count: " + colCount);
            for (int i = 1; i <= colCount; i++) {
                System.out.println("Table Name:   " + rsmd.getTableName(i));
                System.out.println("Column Name:  " + rsmd.getColumnName(i));
                System.out.println("Column Size:  " + rsmd.getColumnDisplaySize(i));
            }
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    /**
     * Always start derby database first in order to connect.
     */
    public void printReport() {
        String query = "SELECT Customer_ID, NAME, CITY FROM Customer";
        try (Connection conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:example");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();
            String col;
            String colData;

            for (int i = 1; i <= colCount; i++) {
                // Left justify column name padded with size spaces
                col = leftJustify(rsmd.getColumnName(i), rsmd.getColumnDisplaySize(i));
                System.out.print(col);
            }

            System.out.println();  // Print a linefeed

            while (rs.next()) {
                for (int i = 1; i <= colCount; i++) {
                    if (rs.getObject(i) != null) {
                        // Get the data in the column as a String
                        colData = rs.getObject(i).toString();
                    } else {
                        // If the column is null use "NULL"
                        colData = "NULL";
                    }
                    col = leftJustify(colData, rsmd.getColumnDisplaySize(i));
                    System.out.print(col);
                }
                System.out.println();
            }
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    private String leftJustify(final String s, int n) {
        int padSpace = n;
        if (s.length() <= n) {
            // Add an extra space if the length of the String s is less than or
            // equal to the length of the column n
            padSpace++;
        }
        // Pad to the right of the String
        return String.format("%1$-" + padSpace + "s", s);
    }

    /**
     * Always start derby database first in order to connect.
     */
    public void moveAroundResultSets() {
        try (Connection conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:example")) {
            DatabaseMetaData dbmd = conn.getMetaData();
            if (dbmd.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY)) {
                System.out.print("Supports TYPE_FORWARD_ONLY");
                if (dbmd.supportsResultSetConcurrency(
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_UPDATABLE)) {
                    System.out.println(" and supports CONCUR_UPDATABLE");
                }
            }

            if (dbmd.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)) {
                System.out.print("Supports TYPE_SCROLL_INSENSITIVE");
                if (dbmd.supportsResultSetConcurrency(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE)) {
                    System.out.println(" and supports CONCUR_UPDATABLE");
                }
            }

            if (dbmd.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE)) {
                System.out.print("Supports TYPE_SCROLL_SENSITIVE");
                if (dbmd.supportsResultSetConcurrency(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE)) {
                    System.out.println(" and supports CONCUR_UPDATABLE");
                }
            }
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    public void demonstrateGetRowCount() {
        String query = "SELECT Customer_ID, NAME, CITY FROM Customer";
        try (Connection conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:example");
                Statement stmt = conn.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = stmt.executeQuery(query)) {
            int rowCount = MyDatabase.getRowCount(rs);
            System.out.println("There are " + rowCount + " rows in CUSTOMER table.");
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    /**
     * Calculate the row count at any time and at any current cursor position.
     * Preserves the current position of the cursor in the ResultSet.
     *
     * @param rs ResulSet Note, this method should only be called on ResultSet
     * objects that are scrollable (type TYPE_SCROLL_INSENSITIVE).
     */
    public static int getRowCount(final ResultSet rs) throws SQLException {
        int rowCount = -1;
        int currRow = 0;

        // make sure the ResultSet is not null
        if (rs != null) {
            // Save the current row position:
            // zero indicates that there is no current row positioin -
            // could be beforeFirst or afterLast
            currRow = rs.getRow();
            // afterLast, so set the currRow negative
            if (rs.isAfterLast()) {
                currRow = -1;
            }
            // move to the last row and get the position
            // if this method returns false, there are no results
            if (rs.last()) {
                // Get the row count
                rowCount = rs.getRow();
                // Return the cursor to the position it was in before the method was called.
                // if the currRow is negative, the cursor position was
                // after the last row, so return the cursor to the last row
                if (currRow == -1) {
                    rs.afterLast();
                    // else if the cursor is zero, move the cursor to before the first row
                } else if (currRow == 0) {
                    rs.beforeFirst();
                    // else return the cursor to its last position
                } else {
                    rs.absolute(currRow);
                }
            }
        }
        return rowCount;
    }

    /**
     * Can be used to modify the contents of a database table, including update
     * existing rows, delete existing rows, and add new rows. Note: an SQL
     * statement that includes a JOIN or an SQL statement with two tables cannot
     * be updated.
     */
    public void updateResultSet() {
        int newCreditLimit = 20_000;  // SUPPRESS CHECKSTYLE MagicNumber
        String query = "SELECT CREDIT_LIMIT FROM Customer WHERE CITY = 'New York'";
        try (Connection conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:example");
                Statement stmt = conn.createStatement(
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                // Check each row: if creditLimit < 20,000 set it to 20,000
                if (rs.getInt("CREDIT_LIMIT") < newCreditLimit) {
                    rs.updateInt("CREDIT_LIMIT", newCreditLimit);
                    // update the row in the database
                    rs.updateRow();

                    if (rs.rowUpdated()) {
                        System.out.println("Row: " + rs.getRow() + " updated.");
                    }
                }
            }
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    /**
     * Doesn't work with the example DB, throws:
     * java.sql.SQLIntegrityConstraintViolationException: Column 'DISCOUNT_CODE'
     * cannot accept a NULL value.
     */
    public void insertNewRow() {
        String query = "SELECT Customer_ID, NAME, CITY FROM Customer";
        try (Connection conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:example");
                Statement stmt = conn.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = stmt.executeQuery(query)) {
            rs.next();
            rs.moveToInsertRow();  // Move the special insert row
            // Create a customer ID
            rs.updateInt("Customer_ID", 2027);  // SUPPRESS CHECKSTYLE MagicNumber
            rs.updateString("NAME", "Spectra");  // Set the name
            rs.updateString("CITY", "Moscow");  // Set the city
            rs.insertRow();  // Insert the row into the database
            rs.moveToCurrentRow();  // Move back to the current row in ResultSet

            if (rs.rowInserted()) {
                System.out.println("Row: " + rs.getRow() + " inserted.");
            }
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    public void getInformationAboutDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:example")) {
            DatabaseMetaData dbmd = conn.getMetaData();
            getColumns(dbmd);
            getProcedures(dbmd);
            getDriverInfo(dbmd);
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    private void getColumns(final DatabaseMetaData dbmd) throws SQLException {
        // Get a ResultSet for any catalog (null) in the APP schema for all
        // tables (%) for all columns (%)
        ResultSet rs = dbmd.getColumns(null, "APP", "%", "%");
        while (rs.next()) {
            System.out.print("Table Name: " + rs.getString("TABLE_NAME") + " ");
            System.out.print("Column Name: " + rs.getString("COLUMN_NAME") + " ");
            System.out.print("Type Name: " + rs.getString("TYPE_NAME") + " ");
            System.out.println("Column Size: " + rs.getString("COLUMN_SIZE"));
        }
    }

    private void getProcedures(final DatabaseMetaData dbmd) throws SQLException {
        // Get a ResultSet of all the stored procedures in any catalog (null) in
        // any schema (null) with wildcard name (%)
        ResultSet rs = dbmd.getProcedures(null, null, "%");
        while (rs.next()) {
            System.out.println("Procedure Name: " + rs.getString("PROCEDURE_NAME"));
        }
    }

    private void getDriverInfo(final DatabaseMetaData dbmd) throws SQLException {
        System.out.println("Driver Name: " + dbmd.getDriverName());
        System.out.println("Driver Version: " + dbmd.getDriverVersion());
        if (dbmd.supportsANSI92EntryLevelSQL()) {
            System.out.println("Driver meets minimum requirements for SQL-92 support.");
        } else {
            System.out.println("Driver does not meet minimum requirements for SQL-92 support.");
        }
        if (dbmd.supportsSavepoints()) {
            System.out.println("Driver supports Savepoints.");
        } else {
            System.out.println("Driver does not support Savepoints.");
        }
    }

    public void usePreparedStatement() {
        String pQuery = "SELECT * FROM Customer WHERE City LIKE ?";
        try (Connection conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:example");
                PreparedStatement pstmt = conn.prepareStatement(pQuery)) {
            // Substitute this String for the first parameter (?)
            pstmt.setString(1, "%New York%");
            ResultSet rs = pstmt.executeQuery();
            processResultSet(rs);
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    public void workWithRowSet() {
        try (JdbcRowSet jrs = RowSetProvider.newFactory().createJdbcRowSet()) {
            String query = "SELECT * FROM Customer WHERE Name LIKE 'Small Bill Company'";
            jrs.setCommand(query);  // Set the query to build the RowSet
            jrs.setUrl(url);
            jrs.setUsername(username);
            jrs.setPassword(password);
            jrs.execute();  // Execute the query stored in setCommand
            processResultSet(jrs);
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    public void demonstrateTransaction() {
        try (Connection conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:example")) {
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            try {
                stmt.executeUpdate("INSERT INTO Discount_Code VALUES ('O', 1.00)");
                stmt.executeUpdate("INSERT INTO Discount_Code VALUES ('P', 2.00)");
                stmt.executeUpdate("INSERT INTO Discount_Code VALUES ('Q', 3.00)");
                // No exception: commit the entire transaction
                conn.commit();
            } catch (SQLException ex) {
                // Rollback the entire transaction if an exception thrown
                conn.rollback();
                throw new SQLException("Failed to insert three new records in DISCOUNT_CODE.", ex);

            }
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    public void demonstrateTransactionUsingSavepoints() {
        try (Connection conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:example")) {
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            String query1 = "INSERT INTO Discount_Code VALUES ('O', 1.00)";
            String query2 = "INSERT INTO Discount_Code VALUES ('P', 2.00)";
            Savepoint sp1 = null;
            try {
                stmt.executeUpdate(query1);
                stmt.executeUpdate(query2);
                // Create a Savepoint for the two inserts so far
                sp1 = conn.setSavepoint();
            } catch (SQLException ex) {
                // If we did not successfully insert two records in DISCOUNT_CODE,
                // rollback the transaction and throw an exception
                conn.rollback();
                throw new SQLException("Failed to insert two new records in DISCOUNT_CODE.", ex);
            }

            String query3 = "INSERT INTO Discount_Code VALUES ('Q', 3.00)";
            try {
                stmt.executeUpdate(query3);
                conn.commit();  // If the whole thing worked, commit
            } catch (SQLException ex) {
                // If the the third insert failed, that's ok, rollback to the
                // Savepoint (rollback the insert ('Q', 3.00)) and commit from there.
                conn.rollback(sp1);
                conn.commit();
            }
            logSQLWarning(conn.getWarnings());  // example how to log SQL Warnings
        } catch (SQLException ex) {
            logSQLException(ex);
        }
    }

    private void logSQLException(SQLException ex) {  // SUPPRESS CHECKSTYLE FinalParameters
        while (ex != null) {
            System.out.println("------ SQLException ------");
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("Vendor Error code: " + ex.getErrorCode());
            System.out.println("Message: " + ex.getMessage());
            System.out.println("Cause: " + ex.getCause().getMessage());
            logSuppressedExceptions(ex);
            ex = ex.getNextException();  // SUPPRESS CHECKSTYLE ParameterAssignment
        }
    }

    private void logSuppressedExceptions(final SQLException ex) {
        Throwable[] suppressed = ex.getSuppressed();
        for (Throwable t : suppressed) {
            System.out.println("Suppressed exception: " + t);
        }
    }

    private void logSQLWarning(SQLWarning warn) {  // SUPPRESS CHECKSTYLE FinalParameters
        while (warn != null) {
            System.out.println("------ SQLWarning ------");
            System.out.println("SQLState: " + warn.getSQLState());
            System.out.println("Vendor Warning code: " + warn.getErrorCode());
            System.out.println("Message: " + warn.getMessage());
            System.out.println("Cause: " + warn.getCause().getMessage());
            warn = warn.getNextWarning();  // SUPPRESS CHECKSTYLE ParameterAssignment
        }
    }

}
