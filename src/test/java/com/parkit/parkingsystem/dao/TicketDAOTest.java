package com.parkit.parkingsystem.dao;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.model.Ticket;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TicketDAOTest {
	
	private static final Logger logger = LogManager.getLogger("TicketDAOTest");
	
	@Mock
	private static DataBaseConfig config;
	
	private static TicketDAO ticketDAO;
	
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
		ticketDAO = new TicketDAO();
		ticketDAO.setDataBaseConfig(config);
    }
    
    @Test
    public void saveTicketException() {
    	boolean result = ticketDAO.saveTicket(new Ticket());
    	assertFalse(result);
    }
    
    @Test
    public void getTicketException() {
    	Ticket result = ticketDAO.getTicket("abc");
    	assertNull(result);
    }
    
    @Test
    public void updateTicketException() {
    	boolean result = ticketDAO.updateTicket(new Ticket());
    	assertFalse(result);
    }
    
}
