package net.floodlightcontroller.mastership;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.mastership.IMastershipService.MastershipCallback;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MastershipManagerTest {
	protected static Logger log = LoggerFactory.getLogger(MastershipManagerTest.class);
	private MastershipManager mm;
	
	@Before
	public void setUp() throws Exception{
		//MockFloodlightProvider fp = new MockFloodlightProvider();
		FloodlightModuleContext fmc = new FloodlightModuleContext();
		
		mm = new MastershipManager();
		
		mm.init(fmc);
		mm.startUp(fmc);
		
	}
	
	@Test
	public void testAcquireMastership(){
		MastershipCallback cb = new MastershipCallback(){
			@Override
			public void changeCallback(long dpid, boolean isMaster) {
				log.info("Callback called!");
			}
		};
		
		long dpid = 1L;
		
		mm.acquireMastership(dpid, cb);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.debug("Is master? {}", mm.amMaster(dpid));
	}
}
