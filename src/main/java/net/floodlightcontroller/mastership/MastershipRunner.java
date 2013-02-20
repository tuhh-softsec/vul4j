package net.floodlightcontroller.mastership;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.mastership.IMastershipService.MastershipCallback;

import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used for lightweight testing of the mastership module without having
 * to load up the entire ONOS.
 * @author jono
 *
 */
public class MastershipRunner {
	protected static Logger log = LoggerFactory.getLogger(MastershipRunner.class);

	public static void main(String args[]){
		FloodlightModuleContext fmc = new FloodlightModuleContext();
		MastershipManager mm = new MastershipManager();
		
		String id = null;
		if (args.length > 0){
			id = args[0];
			log.info("Using unique id: {}", id);
		}
		
		try {
			mm.init(fmc);
			mm.startUp(fmc);
			
			if (id != null){
				mm.setMastershipId(id);
			}
				
			mm.acquireMastership(1L, 
				new MastershipCallback(){
					@Override
					public void changeCallback(long dpid, boolean isMaster) {
						if (isMaster){
							log.debug("Callback for becoming master for {}", HexString.toHexString(dpid));
						}
						else {
							log.debug("Callback for losing mastership for {}", HexString.toHexString(dpid));
						}
					}
				});
			
			mm.registerController(id);
			
			Thread.sleep(5000);
			
			//"Server" loop
			while (true) {
				Thread.sleep(60000);
			}
			
		} catch (FloodlightModuleException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.debug("is master: {}", mm.amMaster(1L));
	}
}
