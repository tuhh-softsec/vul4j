
package com.parkit.parkingsystem.service;

import java.util.Date;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	private TicketDAO ticketDAO;
	
	public FareCalculatorService(TicketDAO ticketDAO) {
		this.ticketDAO = ticketDAO;
	}
	
    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
        	System.out.println(ticket);
        	System.out.println(ticket.getOutTime().before(ticket.getInTime()));
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString()+"  because : inTime="+ticket.getInTime().toString());
        }

        //difference between before and after to get duration, and convert to hours (float, to get percentage)
        long timeMs = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
        double timeSeconds = timeMs/1000;
        
        System.out.println("SECONDES = "+timeSeconds);
        double duration = timeSeconds / 3600;
        
        //free if less than 30 minutes
        if(duration<0.5) {
        	duration = 0;
        }else {
        	//discount 5% if same car
        	int nb = ticketDAO.countTicket(ticket.getVehicleRegNumber());
        	System.out.println("COUNT_CAR="+nb);
        	if(nb>=2) {
        		duration = (duration * Fare.DISCOUNT);
        		System.out.println("DURATION="+duration);
        	}
        }
        
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