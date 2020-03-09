
package com.parkit.parkingsystem.service;

import java.util.Date;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	private static final Logger logger = LogManager.getLogger("FareCalculatorService");
	
	private TicketDAO ticketDAO;
	private HashMap<String,Integer> parking;
	
	public FareCalculatorService() {
		this.parking = new HashMap<String,Integer>();
	}
	
	public FareCalculatorService(TicketDAO ticketDAO) {
		this.parking = new HashMap<String,Integer>();
		this.ticketDAO = ticketDAO;
	}
	
	public void setParking(HashMap<String,Integer> parking) {
		this.parking = parking;
	}
	
    public HashMap<String, Integer> getParking() {
		return parking;
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
        	duration -= 0.5;
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
        //discount 5% (after price) if same car
        Integer nb = this.parking.get(ticket.getVehicleRegNumber());
    	if(nb!=null && nb>=1) {
    		ticket.setPrice(ticket.getPrice()*Fare.DISCOUNT);
    	}
    }
}