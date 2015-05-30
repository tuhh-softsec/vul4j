/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.date_time;

import java.time.LocalDate;
import java.time.Month;

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
        dt.getAndUseDateAndTime();
        dt.getMachineTimestamp();
        dt.convertDatesAndTimesBasedOnTimeZone();
        dt.reportLengthOfEachMonthWithinYear(2015);
        dt.listAllMondaysForGivenMonthThisYear(Month.MAY);
        System.out.println("29 May 2015 occurs on Friday the thirteenth: "
                + dt.doesOccurOnFridayTheThirteenth(LocalDate.of(2015, Month.MAY, 29)));
        System.out.println("13 November 2015 occurs on Friday the thirteenth: "
                + dt.doesOccurOnFridayTheThirteenth(LocalDate.of(2015, Month.NOVEMBER, 13)));
        dt.findDateOfPreviousThursday(LocalDate.now());
    }

}
