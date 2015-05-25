/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.date_time;


public final class DateTimeLauncher {
    
    private DateTimeLauncher() {
    }
    
    public static void main(final String[] args) {
        MyDateTime dt = new MyDateTime();
        
        dt.getCurrentDate();
        dt.getDateFromDateCriteria();
        dt.getYearMonthDate();
        dt.getMonthDayDate();
        dt.getCurrentTimeAndCalculate();
    }
    
}
