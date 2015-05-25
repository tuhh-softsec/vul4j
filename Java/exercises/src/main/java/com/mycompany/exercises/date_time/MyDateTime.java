/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.date_time;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.YearMonth;

/**
 * Java SE 8 Date/Time.
 */
public class MyDateTime {

    public void getCurrentDate() {
        LocalDate date = LocalDate.now();
        System.out.println("Current Date: " + date);

        // Clock that represents the system time, for UK it's BST -01:00
        Clock clock = Clock.systemUTC();
        LocalDate date2 = LocalDate.now(clock);
        System.out.println("Current Date: " + date2);
    }

    public void getDateFromDateCriteria() {
        LocalDate date = LocalDate.of(2015, Month.MAY, 20);
        System.out.println("Date from specified date: " + date);
    }

    public void getYearMonthDate() {
        YearMonth yearMo = YearMonth.now();
        System.out.println("Current Year and month: " + yearMo);
        YearMonth specifiedDate = YearMonth.of(2000, Month.NOVEMBER);
        System.out.println("Specified Year-Month: " + specifiedDate);
    }

    public void getMonthDayDate() {
        MonthDay monthDay = MonthDay.now();
        System.out.println("Current month and day: " + monthDay);
        MonthDay specifiedDate = MonthDay.of(Month.NOVEMBER, 1);
        System.out.println("Specified Month-Day: " + specifiedDate);
    }

    public void getCurrentTimeAndCalculate() {
        LocalTime time = LocalTime.now();
        System.out.println("Current Time: " + time);

        // atDate(LocalDate): obtain the local date and time
        LocalDateTime ldt = time.atDate(LocalDate.of(2011, Month.NOVEMBER, 11));
        System.out.println("Local Date Time object: " + ldt);

        // of(int hours, int min): obtain a specific time
        LocalTime pastTime = LocalTime.of(1, 10);

        // compareTo(LocalTime): compare two time, Positive
        // return value returned if greater
        System.out.println("Comparing times: " + time.compareTo(pastTime));

        // getHour(): return hour in int value (24-hour format
        int hour = time.getHour();
        System.out.println("Hour: " + hour);

        // isAfter(LocalTime): return Boolean comparison
        System.out.println("Is local time after pastTime? " + time.isAfter(pastTime));

        // minusHours(int): Subtract Hous from LocalTime
        LocalTime minusHrs = time.minusHours(5);
        System.out.println("Time minus 5 hours: " + minusHrs);

        // plusMinutes(int): Add minutes to LocalTime
        LocalTime plusMins = time.plusMinutes(30);
        System.out.println("Time plus 30 mins: " + plusMins);
    }
    
    public void getAndUseDateAndTime() {
        
    }
    
}
