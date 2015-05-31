/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.date_time;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

/**
 * The Java Tutorial. A Short Course on the Basics. Sixth Edition.
 * Chapter 21: Date-Time
 * 
 * Evaluates the passed-in date and returns the next payday, assuming that
 * payday occurs twice a month: on the fifteenth and again on the last day of
 * the month. If the computed date occurs on a weekend, then the previous Friday
 * is used.
 */
public class PaydayAdjuster implements TemporalAdjuster {

    /**
     * The adjustInto method accepts a Temporal instance and returns an adjusted
     * LocalDate. If the passed in parameter is not a LocalDate, then a
     * DateTimeException is thrown.
     */
    @Override
    public Temporal adjustInto(final Temporal temporal) {
        LocalDate date = LocalDate.from(temporal);
        int day;
        if (date.getDayOfMonth() < 15) {
            day = 15;
        } else {
            day = date.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        }
        date = date.withDayOfMonth(day);
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
        }
        return temporal.with(date);
    }

}
