package net.onrc.onos.ofcontroller.core.internal;

import java.util.ArrayList;
import java.util.List;

import net.floodlightcontroller.core.IOFSwitch;
import net.onrc.onos.graph.GraphDBConnection;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.ISwitchStorage;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;

import org.openflow.protocol.OFPhysicalPort;
import org.openflow.protocol.OFPhysicalPort.OFPortConfig;
import org.openflow.protocol.OFPhysicalPort.OFPortState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the class for storing the information of switches into CassandraDB
 */
public class SwitchStorageImpl implements ISwitchStorage {
	protected GraphDBOperation op;
	protected static Logger log = LoggerFactory.getLogger(SwitchStorageImpl.class);
	
	/***
	 * Initialize function. Before you use this class, please call this method
	 * @param conf configuration file for Cassandra DB
	 */
	@Override
	public void init(String conf) {
		GraphDBConnection conn = GraphDBConnection.getInstance(conf);
		op = new GraphDBOperation(conn);
	}

	/***
	 * Finalize/close function. After you use this class, please call this method.
	 * It will close the DB connection.
	 */
	public void finalize() {
		close();
	}
	
	/***
	 * Finalize/close function. After you use this class, please call this method.
	 * It will close the DB connection. This is for Java garbage collection.
	 */
	@Override
	public void close() {
		op.close();		
	}
	
	private void setStatus(String dpid, SwitchState state) {
		ISwitchObject sw = op.searchSwitch(dpid);
		
		try {
			if (sw != null) {
				sw.setState(state.toString());
				op.commit();
				log.info("SwitchStorage:setStatus dpid:{} state: {} done", dpid, state);
			    // TODO publish UPDATE_SWITCH event here
			}
		} catch(Exception e) {
			e.printStackTrace();
			op.rollback();
			log.info("SwitchStorage:setStatus dpid:{} state: {} failed: switch not found", dpid, state);	
		}
	}

	/***
	 * This function is for updating the switch into the DB.
	 * @param dpid The switch dpid you want to update from the DB
	 * @param state The state of the switch like ACTIVE, INACTIVE
	 * @param dmope	The DM_OPERATION of the switch
	 */
	@Override
	public void update(String dpid, SwitchState state, DM_OPERATION dmope) {
		log.info("SwitchStorage:update dpid:{} state: {} ", dpid, state);
	    switch(dmope) {
	    	case UPDATE:
	    	case INSERT:
	    	case CREATE:
	            addSwitch(dpid);
	            if (state != SwitchState.ACTIVE) {
	            	setStatus(dpid, state);
	            }
	            break;
	    	case DELETE:
	            deleteSwitch(dpid);
	            break;
	    	default:
	    }
	}

	/***
	 * This function is for adding the switch port into the DB.
	 * @param dpid The switch dpid that has the port.
	 * @param port The port you want to add the switch.
	 */
	@Override
	public void addPort(String dpid, OFPhysicalPort port) {
		
	   if(((OFPortConfig.OFPPC_PORT_DOWN.getValue() & port.getConfig()) > 0) ||
	    					((OFPortState.OFPPS_LINK_DOWN.getValue() & port.getState()) > 0)) {
		     deletePort(dpid, port.getPortNumber());
	         return;  
	   }
	
		try {
			ISwitchObject sw = op.searchSwitch(dpid);
	
	        if (sw != null) {
	        	IPortObject p = op.searchPort(dpid, port.getPortNumber());
	        	log.info("SwitchStorage:addPort dpid:{} port:{}", dpid, port.getPortNumber());
	        	if (p != null) {
	        		log.error("SwitchStorage:addPort dpid:{} port:{} exists setting as ACTIVE", dpid, port.getPortNumber());
	        		p.setState("ACTIVE");
	        		p.setPortState(port.getState());
	        		p.setDesc(port.getName());
	        		op.commit();
	        		
	    		    // TODO publish UPDATE_PORT event here
	        	} else {
	        		p = op.newPort(dpid, port.getPortNumber());
	        		p.setState("ACTIVE");
	        		p.setPortState(port.getState());
	        		p.setDesc(port.getName());
	        		sw.addPort(p);
	        		op.commit();
	        		
	    		    // TODO publish ADD_PORT event here
	        	}
	        } else {
	    		log.error("SwitchStorage:addPort dpid:{} port:{} : failed switch does not exist", dpid, port.getPortNumber());
	        }
		} catch (Exception e) {
			e.printStackTrace();
			op.rollback();
			log.error("SwitchStorage:addPort dpid:{} port:{} failed", dpid, port.getPortNumber());
		}	
	
	}

	/***
	 * This function is for deleting the switch port from the DB.
	 * @param dpid The switch dpid that has the port.
	 * @param port The port you want to delete the switch.
	 */
	@Override
	public void deletePort(String dpid, short port) {
		try {
			ISwitchObject sw = op.searchSwitch(dpid);
	
	        if (sw != null) {
	        	IPortObject p = op.searchPort(dpid, port);
	            if (p != null) {
	        		log.info("SwitchStorage:deletePort dpid:{} port:{} found and set INACTIVE", dpid, port);
	        		p.setState("INACTIVE");
	        		op.commit();
	    		    // TODO publish DELETE_PORT event here
	        	}
	        }
		} catch (Exception e) {
			e.printStackTrace();
			op.rollback();
			log.info("SwitchStorage:deletePort dpid:{} port:{} failed", dpid, port);
		}	
	}

	@Override
	public void addSwitch(IOFSwitch sw) {
		String dpid = sw.getStringId();
		log.info("SwitchStorage:addSwitch(): dpid {} ", dpid);
		
		ISwitchObject curr = op.searchSwitch(dpid);
		if (curr != null) {
			//If existing the switch. set The SW state ACTIVE. 
			log.info("SwitchStorage:addSwitch dpid:{} already exists", dpid);
			setSwitchStateImpl(curr, SwitchState.ACTIVE);
		} else {
			curr = addSwitchImpl(dpid);
		}

		try {
			List<IPortObject> added_ports = new ArrayList<IPortObject>();
			List<IPortObject> updated_ports = new ArrayList<IPortObject>();
			for (OFPhysicalPort port: sw.getPorts()) {
				IPortObject p = op.searchPort(dpid, port.getPortNumber());
				if (p != null) {
		    		log.error("SwitchStorage:addPort dpid:{} port:{} exists", dpid, port.getPortNumber());
		    		p.setState("ACTIVE");
		    		p.setPortState(port.getState());
		    		p.setDesc(port.getName());
		    		
		       		added_ports.add(p);
				} else {
		    		p = op.newPort(dpid, port.getPortNumber());
		    		p.setState("ACTIVE");
		    		p.setPortState(port.getState());
		    		p.setDesc(port.getName());           		
		    		curr.addPort(p);
		    		
		       		updated_ports.add(p);
				}         		
			}
	
			op.commit();
		    
		    // TODO publish ADD_PORT event here
		    // TODO publish UPDATE_PORT event here
		} catch (Exception e) {
			e.printStackTrace();
			op.rollback();
			log.info("SwitchStorage:addSwitch dpid:{} failed", dpid);
		}		
	}

	/***
	 * This function is for adding the switch into the DB.
	 * @param dpid The switch dpid you want to add into the DB.
	 */
	@Override
	public void addSwitch(String dpid) {
		log.info("SwitchStorage:addSwitch(): dpid {} ", dpid);
		
		ISwitchObject sw = op.searchSwitch(dpid);
		if (sw != null) {
			//If existing the switch. set The SW state ACTIVE. 
			log.info("SwitchStorage:addSwitch dpid:{} already exists", dpid);
			setSwitchStateImpl(sw, SwitchState.ACTIVE);
		} else {
			addSwitchImpl(dpid);
		}
	}
	
	private ISwitchObject addSwitchImpl(String dpid) {
		try {
			ISwitchObject sw = op.newSwitch(dpid);
			if (sw != null) {
				sw.setState(SwitchState.ACTIVE.toString());
				log.info("SwitchStorage:addSwitchImpl dpid:{} added", dpid);
			} else {
				log.error("switchStorage:addSwitchImpl dpid:{} failed", dpid);
				throw new RuntimeException();
			}

            op.commit();
            
            // TODO publish ADD_SWITCH event here
    		return sw;
		} catch (Exception e) {
			e.printStackTrace();
			op.rollback();
			log.info("SwitchStorage:addSwitchImpl dpid:{} failed", dpid);
			return null;
		}
	}
	
	private void setSwitchStateImpl(ISwitchObject sw, SwitchState state) {
		if (sw != null && state != null) {
			sw.setState(state.toString());
            op.commit();
            
			// TODO publish UPDATE_SWITCH event here
		}
	}

	/***
	 * This function is for deleting the switch into the DB.
	 * @param dpid The switch dpid you want to delete from the DB.
	 */
	@Override
	public void deleteSwitch(String dpid) {
		try {
			ISwitchObject sw = op.searchSwitch(dpid);
            if (sw  != null) {
            	op.removeSwitch(sw);
            	op.commit();
            	log.info("SwitchStorage:DeleteSwitch dpid:{} done", dpid);

            	// TODO publish DELETE_SWITCH event here
            }
		} catch (Exception e) {
			e.printStackTrace();
			op.rollback();			
			log.error("SwitchStorage:deleteSwitch {} failed", dpid);
		}

	}
	
}