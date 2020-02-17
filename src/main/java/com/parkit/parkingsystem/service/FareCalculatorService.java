
package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
        	System.out.println(ticket);
        	System.out.println(ticket.getOutTime().before(ticket.getInTime()));
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString()+"  because : inTime="+ticket.getInTime().toString());
        }

        //difference between before and after to get duration, and convert to hours (float, to get percentage)
        long timeMs = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
        float timeSeconds = timeMs/1000;
        float duration = timeSeconds / 3600;
        
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}