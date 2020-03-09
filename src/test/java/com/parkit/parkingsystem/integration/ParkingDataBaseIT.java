package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static final Logger logger = LogManager.getLogger("ParkingDataBaseIT");
	
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    
    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }

    @AfterAll
    private static void tearDown(){

    }
    
    
    private void parkCar(String carNumber, Date inTime) {
//    	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    	parkingService.processIncomingVehicle(inTime);
    	//TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
        try{
        	Ticket ticket = ticketDAO.getTicket(carNumber);
        	assertNotNull(ticket);
        	assertNull(ticket.getOutTime());
        	System.out.println("outtime = "+ticket);
        	ParkingSpot parkingSpot = parkingSpotDAO.getParkingSpot(ticket.getParkingSpot().getId());
        	assertFalse(parkingSpot.isAvailable());
        }catch(Exception e){
        	fail("cannot get car number");
        }
    }
    
    private void exitCar(String carNumber, Date outTime) {
//    	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle(outTime);
        //TODO: check that the fare generated and out time are populated correctly in the database
        try {
        	Ticket ticket = ticketDAO.getTicket(carNumber);
        	System.out.println(ticket);
        	assertNotNull(ticket.getOutTime());
        	ParkingSpot parkingSpot = parkingSpotDAO.getParkingSpot(ticket.getParkingSpot().getId());
        	assertTrue(parkingSpot.isAvailable());
		} catch (Exception e) {
			fail("testParkingLotExit");
		}
    }

    @Test
    public void testParkingACar(){
    	try {
			parkCar(inputReaderUtil.readVehicleRegistrationNumber(), new Date());
		} catch (Exception e) {
			logger.error("cannot read user input car number",e);
		}
    }

    @Test
    public void testParkingLotExit(){
        testParkingACar();
        
        Date outTime = new Date();
        outTime.setTime(System.currentTimeMillis() + ( 60 * 1000));
        try {
			exitCar(inputReaderUtil.readVehicleRegistrationNumber(), outTime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Test
    public void testSameCarParking(){
    	Date time = new Date();
    	try {
    		String carNumber = inputReaderUtil.readVehicleRegistrationNumber();
			parkCar(carNumber, time);
			time.setTime(time.getTime()+(60*1000));
			exitCar(carNumber, time);
			
			//the same car come back for parking after some time
			time.setTime(time.getTime()+(60*1000));
			parkCar(carNumber, time);
		} catch (Exception e) {
			logger.error("cannot read user input car number",e);
		}
    }
    
    @Test
    public void testDiscountForSameCar() {
    	Date oneHourAfter = new Date();
    	Date twoHoursAfter = new Date();
    	Date threeHoursAfter = new Date();
    	Date fourHoursAfter = new Date();
    	
    	long time = 1582053077028L;
    	oneHourAfter.setTime(time);
    	time += (60*60*1000);
    	twoHoursAfter.setTime(time);
    	time += (60*60*1000);
    	threeHoursAfter.setTime(time);
    	time += (60*60*1000);
    	fourHoursAfter.setTime(time);
    	
    	try {
    		String carNumber = inputReaderUtil.readVehicleRegistrationNumber();
    		
    		//1 hour parking
			parkCar(carNumber, oneHourAfter);
			exitCar(carNumber, twoHoursAfter);
			Ticket ticket = ticketDAO.getTicket(carNumber);
			assertEquals(Fare.CAR_RATE_PER_HOUR/2,ticket.getPrice());
			
			//the same car come back for parking after some time
			parkCar(carNumber, threeHoursAfter);
			exitCar(carNumber, fourHoursAfter);
			ticket = ticketDAO.getTicket(carNumber);
			//5% discount if parking same car
			assertEquals(0.95*Fare.CAR_RATE_PER_HOUR/2,ticket.getPrice());
		} catch (Exception e) {
			logger.error("cannot read user input car number",e);
		}
    }
    

}
