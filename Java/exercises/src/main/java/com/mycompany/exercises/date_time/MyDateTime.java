/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.date_time;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.zone.ZoneRules;
import java.util.Arrays;
import java.util.stream.IntStream;

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
        LocalDateTime ldt = LocalDateTime.now();
        System.out.println("Local Date and Time: " + ldt);

        // Obtain the LocalDateTime object of the date 11/11/2000 at 12:00
        LocalDateTime ldt2 = LocalDateTime.of(2000, Month.NOVEMBER, 11, 12, 00);
        System.out.println("Local Date and Time of 11/11/2000 at 12:00: " + ldt2);

        // Obtain the month from LocalDateTime object
        Month month = ldt.getMonth();
        int monthValue = ldt.getMonthValue();
        System.out.println("Month: " + month);
        System.out.println("Month Value: " + monthValue);

        // Obtain day of Month, Week and Year
        int day = ldt.getDayOfMonth();
        DayOfWeek dayWeek = ldt.getDayOfWeek();
        int dayOfYr = ldt.getDayOfYear();
        System.out.println("Day: " + day);
        System.out.println("Day of Week: " + dayWeek);
        System.out.println("Day of Year: " + dayOfYr);

        // Obtain year
        int year = ldt.getYear();
        System.out.println("Date: " + monthValue + "/" + day + "/" + year);

        // Obtain current time
        int hour = ldt.getHour();
        int minute = ldt.getMinute();
        int second = ldt.getSecond();
        System.out.println("Current Time: " + hour + ":" + minute + ":" + second);

        // Calculation of Months, etc.
        LocalDateTime currMinusMonths = ldt.minusMonths(12);
        LocalDateTime currMinusHours = ldt.minusHours(10);
        LocalDateTime currPlusDays = ldt.plusDays(30);
        System.out.println("Current Date and Time Minus 12 Months: " + currMinusMonths);
        System.out.println("Current Date and Time Minus 10 Hours: " + currMinusHours);
        System.out.println("Current Date and Time Plus 30 Days: " + currPlusDays);
    }

    /**
     * Instant is the most accurate timestamp as it’s based on nanosecond
     * accuracy.
     */
    public void getMachineTimestamp() {
        // shows GMT time, while actual BST time is +01:00
        Instant timestamp = Instant.now();
        System.out.println("The current timestamp: " + timestamp);

        // Now minus three days
        Instant minusThree = timestamp.minus(3, ChronoUnit.DAYS);
        System.out.println("Now minus three days: " + minusThree);

        ZonedDateTime atZone = timestamp.atZone(ZoneId.of("GMT"));
        System.out.println("At zone GMT: " + atZone);

        Instant yesterday = Instant.now().minus(24, ChronoUnit.HOURS);
        System.out.println("Yesterday: " + yesterday);
    }

    public void convertDatesAndTimesBasedOnTimeZone() {
        LocalDateTime checkOut = LocalDateTime.of(2014, 12, 13, 13, 00);
        ZoneId checkOutZone = ZoneId.of("US/Eastern");
        LocalDateTime checkIn = LocalDateTime.of(2014, 12, 18, 10, 00);
        ZoneId checkInZone = ZoneId.of("US/Mountain");
        scheduleReport(checkOut, checkOutZone, checkIn, checkInZone);
    }

    private void scheduleReport(final LocalDateTime checkOut, final ZoneId checkOutZone,
            final LocalDateTime checkIn, final ZoneId checkInZone) {
        ZonedDateTime beginTrip = ZonedDateTime.of(checkOut, checkOutZone);
        System.out.println("Trip Begins: " + beginTrip);

        // Get the rules of the check out time zone
        ZoneRules checkOutZoneRules = checkOutZone.getRules();
        System.out.println("Checkout Time Zone Rules: " + checkOutZoneRules);

        // If the trip took 4 days
        ZonedDateTime beginPlus = beginTrip.plusDays(4);
        System.out.println("Four Days Later: " + beginPlus);

        // End of trip in starting timne zone
        ZonedDateTime endTripOriginalZone = ZonedDateTime.of(checkIn, checkOutZone);
        ZonedDateTime endTrip = ZonedDateTime.of(checkIn, checkInZone);
        int diff = endTripOriginalZone.compareTo(endTrip);
        String diffStr = diff >= 0 ? "NO" : "YES";
        System.out.println("End trip date/time in original zone: " + endTripOriginalZone);
        System.out.println("End trip date/time in check-in zone: " + endTrip);
        System.out.println("Original Zone Time is less than new zone time? " + diffStr);

        ZoneId checkOutZoneId = beginTrip.getZone();
        ZoneOffset checkOutOffset = beginTrip.getOffset();
        ZoneId checkInZoneId = endTrip.getZone();
        ZoneOffset checkInOffset = endTrip.getOffset();

        System.out.println("Check out zone and offset: " + checkOutZoneId + checkOutOffset);
        System.out.println("Check in zone and offset: " + checkInZoneId + checkInOffset);
    }

    public void compareTwoDates() {

    }

    /**
     * The Java Tutorial. A Short Course on the Basics. Sixth Edition.
     * Chapter 21: Date-Time
     * Exercise 1
     * Solution: http://docs.oracle.com/javase/tutorial/datetime/iso/QandE/MonthsInYear.java
     *
     * @param year
     */
    public void reportLengthOfEachMonthWithinYear(int year) {
        System.out.println("Year: " + year);
        Arrays.stream(Month.values())
                .forEach(month -> {
                    YearMonth yearMonth = YearMonth.of(year, month);
                    int lengthOfMonth = yearMonth.lengthOfMonth();
                    System.out.println(month + ": " + lengthOfMonth + " days");
                });
    }

    /**
     * The Java Tutorial. A Short Course on the Basics. Sixth Edition.
     * Chapter 21: Date-Time
     * Exercise 2
     * Solution: http://docs.oracle.com/javase/tutorial/datetime/iso/QandE/ListMondays.java
     *
     * @param month
     */
    public void listAllMondaysForGivenMonthThisYear(final Month month) {
        Year currentYear = Year.now();
        YearMonth yearMonth = currentYear.atMonth(month);
        System.out.println("Mondays of " + yearMonth + ":");
        IntStream.rangeClosed(1, yearMonth.lengthOfMonth())
                .forEach(i -> {
                    LocalDate date = LocalDate.of(currentYear.getValue(), month, i);
                    DayOfWeek dotw = date.getDayOfWeek();
                    if (dotw == DayOfWeek.MONDAY) {
                        System.out.println(date.getDayOfMonth());
                    }
                });
    }

    /**
     * The Java Tutorial. A Short Course on the Basics. Sixth Edition.
     * Chapter 21: Date-Time
     * Exercise 3
     * Solution: http://docs.oracle.com/javase/tutorial/datetime/iso/QandE/Superstitious.java
     *           http://docs.oracle.com/javase/tutorial/datetime/iso/QandE/FridayThirteenQuery.java
     *
     * @param date return true if given date occurs on Friday the thirteenth
     */
    public boolean doesOccurOnFridayTheThirteenth(final LocalDate date) {
        DayOfWeek dotw = date.getDayOfWeek();
        int dotm = date.getDayOfMonth();
        return dotw == DayOfWeek.FRIDAY && dotm == 13;
    }

    public void findDateOfPreviousThursday(final LocalDate date) {
        LocalDate previousThursday = date.with(TemporalAdjusters.previous(DayOfWeek.THURSDAY));
        System.out.println("The previous Thursday is: " + previousThursday);
    }
    
    public void findNextWednesday(final LocalDate date) {
        TemporalAdjuster adj = TemporalAdjusters.next(DayOfWeek.WEDNESDAY);
        LocalDate nextWed = date.with(adj);
        System.out.println("The next Wednesday is: " + nextWed);
    }

    public ZonedDateTime convertToZonedDateTime(final Instant inst) {
        // #1
        return ZonedDateTime.ofInstant(inst, ZoneId.systemDefault());

        // #2
        // return inst.atZone(ZoneId.systemDefault());
    }

    public Instant convertToInstant(final ZonedDateTime zdt) {
        return zdt.toInstant();
    }

    public void getSecondsSinceBeginningOfJavaEpoch() {
        long secondsFromEpoch = Instant.ofEpochSecond(0L).until(Instant.now(), ChronoUnit.SECONDS);
        System.out.println("Seconds since the beginning of the Java epoch: " + secondsFromEpoch);
    }

    public void getNextPayday() {
        LocalDate date = LocalDate.now();
        LocalDate nextPayday = date.with(new PaydayAdjuster());
        System.out.println("Next pay day is on " + nextPayday + ".");
    }

    public void isImportantDay() {
        LocalDate date = LocalDate.of(2015, Month.AUGUST, 10);
        // Invoke the query
        Boolean isFamilyHolidays = date.query(new FamilyHolidays());
        System.out.println(date + " is an important date: " + isFamilyHolidays + ".");
    }
    
    public void reportHowOldYouAre(final LocalDate birthday) {
        LocalDate today = LocalDate.now();

        Period p = Period.between(birthday, today);
        long p2 = ChronoUnit.DAYS.between(birthday, today);
        System.out.println("You are " + p.getYears() + " years, " + p.getMonths()
                + " months, and " + p.getDays()
                + " days old. (" + p2 + " days total)");
    }
    
    public void calculateHowLongUntilYourNextBirthday(final LocalDate birthday) {
        LocalDate today = LocalDate.now();

        LocalDate nextBDay = birthday.withYear(today.getYear());
        // If your birthday has occurred this year already, add 1 to the year.
        if (nextBDay.isBefore(today) || nextBDay.isEqual(today)) {
            nextBDay = nextBDay.plusYears(1);
        }
        Period p = Period.between(today, nextBDay);
        long p2 = ChronoUnit.DAYS.between(today, nextBDay);
        System.out.println("There are " + p.getMonths() + " months, and "
                + p.getDays() + " days until your next birthday. ("
                + p2 + " total)");
    }

}
