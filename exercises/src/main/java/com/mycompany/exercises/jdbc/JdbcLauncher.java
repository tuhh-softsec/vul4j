/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.jdbc;

/**
 * Helper class to execute other classes.
 */
public final class JdbcLauncher {

    private JdbcLauncher() {
    }

    public static void main(final String[] args) {
        MyDatabase db = new MyDatabase();
        
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
