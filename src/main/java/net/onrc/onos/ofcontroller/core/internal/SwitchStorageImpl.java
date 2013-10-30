package net.onrc.onos.ofcontroller.core.internal;

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
 * This is the class for storing the information of switches into GraphDB
 */
public class SwitchStorageImpl implements ISwitchStorage {
	protected GraphDBOperation op;
	protected final static Logger log = LoggerFactory.getLogger(SwitchStorageImpl.class);
	
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
	
	// Method designing policy:
	//  op.commit() and op.rollback() MUST called in public (first-class) methods.
	//  A first-class method MUST NOT call other first-class method.
	//  Routine process should be implemented in private method.
	//  A private method MUST NOT call commit or rollback.
	
	/***
	 * This function is for updating the switch into the DB.
	 * @param dpid The switch dpid you want to update from the DB
	 * @param state The state of the switch like ACTIVE, INACTIVE
	 * @param dmope	The DM_OPERATION of the switch
	 */
	@Override
	public boolean updateSwitch(String dpid, SwitchState state, DM_OPERATION dmope) {
		boolean success = false;
		ISwitchObject sw = null;
		
		log.info("SwitchStorage:update {} dpid:{}", dmope, dpid);
	    switch(dmope) {
	    	case UPDATE:
            	try {
		    		sw = op.searchSwitch(dpid);
		    		if (sw != null) {
			            	setSwitchStateImpl(sw, state);
							op.commit();
							success = true;
		    		}
				} catch (Exception e) {
					op.rollback();
					e.printStackTrace();
					log.info("SwitchStorage:update {} dpid:{} failed", dmope, dpid);
				}
	    		break;
	    	case INSERT:
	    	case CREATE:
            	try {
		            sw = addSwitchImpl(dpid);
		            if (sw != null) {
			            if (state != SwitchState.ACTIVE) {
			            	setSwitchStateImpl(sw, state);
			            }
						op.commit();
						success = true;
		            }
				} catch (Exception e) {
					op.rollback();
					e.printStackTrace();
					log.info("SwitchStorage:update {} dpid:{} failed", dmope, dpid);
				}
	            break;
	    	case DELETE:
	            try {
		    		sw = op.searchSwitch(dpid);
		    		if (sw != null) {
				            deleteSwitchImpl(sw);
							op.commit();
							success = true;
		    		}
				} catch (Exception e) {
					op.rollback();
					e.printStackTrace();
					log.info("SwitchStorage:update {} dpid:{} failed", dmope, dpid);
				}
	            break;
	    	default:
	    }
	    
	    return success;
	}
	
	@Override
	public boolean addSwitch(IOFSwitch sw) {
		boolean success = false;
		
		String dpid = sw.getStringId();
		log.info("SwitchStorage:addSwitch(): dpid {} ", dpid);
		
		try {
			ISwitchObject curr = op.searchSwitch(dpid);
			if (curr != null) {
				//If existing the switch. set The SW state ACTIVE. 
				log.info("SwitchStorage:addSwitch dpid:{} already exists", dpid);
				setSwitchStateImpl(curr, SwitchState.ACTIVE);
			} else {
				curr = addSwitchImpl(dpid);
			}
	
			for (OFPhysicalPort port: sw.getPorts()) {
				IPortObject p = op.searchPort(dpid, port.getPortNumber());
				if (p != null) {
		    		log.debug("SwitchStorage:addPort dpid:{} port:{} exists", dpid, port.getPortNumber());
		    		setPortStateImpl(p, port.getState(), port.getName());
		    		p.setState("ACTIVE");
		    		if (curr.getPort(port.getPortNumber()) == null) {
		    			// The port exists but the switch has no "on" link to it
		    			curr.addPort(p);
		    		}
				} else {
					p = addPortImpl(curr, port.getPortNumber());
					setPortStateImpl(p, port.getState(), port.getName());
				}         		
			}
			op.commit();
			success = true;
		} catch (Exception e) {
			op.rollback();
			//e.printStackTrace();
			log.error("SwitchStorage:addSwitch dpid:{} failed: {}", dpid);
			log.error("switch write error", e);
		}
		
		return success;
	}

	/***
	 * This function is for adding the switch into the DB.
	 * @param dpid The switch dpid you want to add into the DB.
	 */
	// This method is only called by tests, so we probably don't need it.
	// If we need both addSwitch interfaces, one should call the other
	// rather than implementing the same logic twice.
	@Deprecated 
	@Override
	public boolean addSwitch(String dpid) {
		boolean success = false;
		
		log.info("SwitchStorage:addSwitch(): dpid {} ", dpid);
		try {
			ISwitchObject sw = op.searchSwitch(dpid);
			if (sw != null) {
				//If existing the switch. set The SW state ACTIVE. 
				log.info("SwitchStorage:addSwitch dpid:{} already exists", dpid);
				setSwitchStateImpl(sw, SwitchState.ACTIVE);
			} else {
				addSwitchImpl(dpid);
			}
			op.commit();
			success = true;
		} catch (Exception e) {
			op.rollback();
			e.printStackTrace();
			log.info("SwitchStorage:addSwitch dpid:{} failed", dpid);
		}
		
		return success;
	}

	/***
	 * This function is for deleting the switch into the DB.
	 * @param dpid The switch dpid you want to delete from the DB.
	 */
	@Override
	public boolean deleteSwitch(String dpid) {
		boolean success = false;
		
		try {
			ISwitchObject sw = op.searchSwitch(dpid);
			if (sw != null) {
				deleteSwitchImpl(sw);
	        	op.commit();
			}
			success = true;
		} catch (Exception e) {
			op.rollback();
			e.printStackTrace();
			log.error("SwitchStorage:deleteSwitch {} failed", dpid);
		}
	
		return success;
	}

	public boolean updatePort(String dpid, short portNum, int state, String desc) {
		boolean success = false;
		
		try {
			ISwitchObject sw = op.searchSwitch(dpid);
	
	        if (sw != null) {
	        	IPortObject p = sw.getPort(portNum);
	        	log.info("SwitchStorage:updatePort dpid:{} port:{}", dpid, portNum);
	        	if (p != null) {
	        		setPortStateImpl(p, state, desc);
	        	}
	        	op.commit();
        		success = true;
	        } else {
	    		log.error("SwitchStorage:updatePort dpid:{} port:{} : failed switch does not exist", dpid, portNum);
	        }
		} catch (Exception e) {
			op.rollback();
			e.printStackTrace();
			log.error("SwitchStorage:addPort dpid:{} port:{} failed", dpid, portNum);
		}	

		return success;
	}

	/***
	 * This function is for adding the switch port into the DB.
	 * @param dpid The switch dpid that has the port.
	 * @param phport The port you want to add the switch.
	 */
	@Override
	public boolean addPort(String dpid, OFPhysicalPort phport) {
		boolean success = false;
		
		if(((OFPortConfig.OFPPC_PORT_DOWN.getValue() & phport.getConfig()) > 0) ||
				((OFPortState.OFPPS_LINK_DOWN.getValue() & phport.getState()) > 0)) {
			// just dispatch to deletePort()
			return deletePort(dpid, phport.getPortNumber());
		}
	
		try {
			ISwitchObject sw = op.searchSwitch(dpid);
	
	        if (sw != null) {
	        	IPortObject p = sw.getPort(phport.getPortNumber());
	        	log.info("SwitchStorage:addPort dpid:{} port:{}", dpid, phport.getPortNumber());
	        	if (p != null) {
	        		setPortStateImpl(p, phport.getState(), phport.getName());
	        		log.error("SwitchStorage:addPort dpid:{} port:{} exists setting as ACTIVE", dpid, phport.getPortNumber());
	        	} else {
	        		addPortImpl(sw, phport.getPortNumber());
	        		setPortStateImpl(p, phport.getState(), phport.getName());
	        	}
        		op.commit();
        		success = true;
	        } else {
	    		log.error("SwitchStorage:addPort dpid:{} port:{} : failed switch does not exist", dpid, phport.getPortNumber());
	        }
		} catch (Exception e) {
			op.rollback();
			e.printStackTrace();
			log.error("SwitchStorage:addPort dpid:{} port:{} failed", dpid, phport.getPortNumber());
		}	

		return success;
	}

	/***
	 * This function is for deleting the switch port from the DB.
	 * @param dpid The switch dpid that has the port.
	 * @param port The port you want to delete the switch.
	 */
	@Override
	public boolean deletePort(String dpid, short port) {
		boolean success = false;
		
		try {
			ISwitchObject sw = op.searchSwitch(dpid);
	
	        if (sw != null) {
	        	IPortObject p = sw.getPort(port);
	            if (p != null) {
	        		log.info("SwitchStorage:deletePort dpid:{} port:{} found and set INACTIVE", dpid, port);
	        		deletePortImpl(p);
	        		op.commit();
	        	}
	        }
		} catch (Exception e) {
			op.rollback();
			e.printStackTrace();
			log.info("SwitchStorage:deletePort dpid:{} port:{} failed", dpid, port);
		}

		return success;
	}

	private ISwitchObject addSwitchImpl(String dpid) {
		if (dpid != null) {
			ISwitchObject sw = op.newSwitch(dpid);
			sw.setState(SwitchState.ACTIVE.toString());
			log.info("SwitchStorage:addSwitchImpl dpid:{} added", dpid);
			return sw;
		} else {
			return null;
		}
	}
	
	private void setSwitchStateImpl(ISwitchObject sw, SwitchState state) {
		if (sw != null && state != null) {
			sw.setState(state.toString());
			log.info("SwitchStorage:setSwitchStateImpl dpid:{} updated {}",
					sw.getDPID(), state.toString());
		}
	}
	
	private void deleteSwitchImpl(ISwitchObject sw) {
        if (sw  != null) {
        	op.removeSwitch(sw);
        	log.info("SwitchStorage:DeleteSwitchImpl dpid:{} done",
        			sw.getDPID());
        }
	}

	private IPortObject addPortImpl(ISwitchObject sw, short portNum) {
		IPortObject p = op.newPort(sw.getDPID(), portNum);
		p.setState("ACTIVE");
		sw.addPort(p);
    	log.info("SwitchStorage:addPortImpl dpid:{} port:{} done",
    			sw.getDPID(), portNum);
		
		return p;
	}

	private void setPortStateImpl(IPortObject port, Integer state, String desc) {
		if (port != null) {
			if (state != null) {
				port.setPortState(state);
			}
			if (desc != null) {
				port.setDesc(desc);
			}
			
	    	log.info("SwitchStorage:setPortStateImpl port:{} state:{} desc:{} done",
	    			new Object[] {port.getPortId(), state, desc});
		}
	}
	
	private void deletePortImpl(IPortObject port) {
		if (port != null) {
			op.removePort(port);
	    	log.info("SwitchStorage:deletePortImpl port:{} done",
	    			port.getPortId());
		}
	}
}