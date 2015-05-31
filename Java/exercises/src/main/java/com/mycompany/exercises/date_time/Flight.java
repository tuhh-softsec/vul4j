/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.exercises.date_time;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The Java Tutorial. A Short Course on the Basics. Sixth Edition.
 * Chapter 21: Date-Time
 *
 * 650-minute flight from San Francisco to Tokyo.
 */
public class Flight {

    public void fligh() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MMM d yyy hh:mm a");

        // Leaving from San Francisco on July 20, 2013, at 7:30 p.m.
        LocalDateTime leaving = LocalDateTime.of(2013, Month.JULY, 20, 19, 30);
        ZoneId leavingZone = ZoneId.of("America/Los_Angeles");
        ZonedDateTime departure = ZonedDateTime.of(leaving, leavingZone);

        try {
            String out1 = departure.format(format);
            System.out.printf("LEAVING: %s (%s)%n", out1, leavingZone);
        } catch (DateTimeException ex) {
            System.out.printf("%s can't be formatted!%n", departure);
            throw ex;
        }

        // Flight is 10 hours and 50 minutes, or 650 minutes
        ZoneId arrivingZone = ZoneId.of("Asia/Tokyo");
        ZonedDateTime arrival = departure.withZoneSameInstant(arrivingZone).plusMinutes(650);

        try {
            String out2 = arrival.format(format);
            System.out.printf("ARRIVING: %s (%s)%n", out2, arrivingZone);
        } catch (DateTimeException ex) {
            System.out.printf("%s can't be formatted!%n", arrival);
            throw ex;
        }

        if (arrivingZone.getRules().isDaylightSavings(arrival.toInstant())) {
            System.out.printf(" (%s daylight saving time will be in effect.)%n", arrivingZone);
        } else {
            System.out.printf(" (%s standard time will be in effect.)%n", arrivingZone);
        }
    }

}
