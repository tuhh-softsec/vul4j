package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ParkingSpotDAOTest {
private static final Logger logger = LogManager.getLogger("ParkingSpotDAOTest");
	
	@Mock
	private static DataBaseConfig config;
	
	private static ParkingSpotDAO parkingSpotDAO;
	
	@BeforeAll
    private static void setUp(){
		
    }

    @BeforeEach
    private void setUpPerTest() {
    	try {
			when(config.getConnection()).thenReturn(null);
		} catch (Exception e) {
			logger.debug("cannot mock",e);
		}
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.setDataBaseConfig(config);
    }
    
    @Test
    public void getParkingException() {
    	ParkingSpot parkingSpot = parkingSpotDAO.getParkingSpot(1);
    	assertNull(parkingSpot);
    }
    
    @Test
    public void getNextParkingException() {
    	int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE);
    	assertEquals(-1,result);
    }
    
    @Test
    public void updateParkingException() {
    	boolean result = parkingSpotDAO.updateParking(new ParkingSpot());
    	assertFalse(result);
    }
}
