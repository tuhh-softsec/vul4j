package net.onrc.onos.ofcontroller.core.internal;

import java.util.ArrayList;
import java.util.List;

import com.tinkerpop.blueprints.impls.ramcloud.PerfMon;

import net.floodlightcontroller.core.IOFSwitch;
import net.onrc.onos.graph.DBOperation;
import net.onrc.onos.graph.GraphDBManager;
import net.onrc.onos.ofcontroller.core.ISwitchStorage;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.ISwitchStorage;

import org.openflow.protocol.OFPhysicalPort;
import org.openflow.protocol.OFPhysicalPort.OFPortConfig;
import org.openflow.protocol.OFPhysicalPort.OFPortState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the class for storing the information of switches into GraphDB
 */
public class SwitchStorageImpl implements ISwitchStorage {

	protected DBOperation op;
	protected final static Logger log = LoggerFactory.getLogger(SwitchStorageImpl.class);
	public final long measureONOSTimeProp = Long.valueOf(System.getProperty("benchmark.measureONOS", "0"));
	public final long measureAllTimeProp = Long.valueOf(System.getProperty("benchmark.measureAll", "0"));

	private static PerfMon pm = PerfMon.getInstance();

	/***
	 * Initialize function. Before you use this class, please call this method
	 * @param conf configuration file for Cassandra DB
	 */
	@Override
	public void init(final String dbStore, final String conf) {
		op = GraphDBManager.getDBOperation();
	}


	/***
	 * Finalize/close function. After you use this class, please call this method.
	 * It will close the DB connection.
	 */
	@Override
	protected void finalize() {
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
	/*
	 * Jono, 11/8/2013
	 * We don't need this update method that demultiplexes DM_OPERATIONS,
	 * we can have clients just call the required methods directly.
	 * We especially don't need this update method to re-implement
	 * the functions of other methods.
	 */
	@Deprecated
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
                long startSwitchTime = 0, endSwitchTime = 0;
                long startUpdSwitchTime = 0, endUpdSwitchTime=0;
                long startPortTime = 0, endPortTime=0;
                long totalStartTime =0, totalEndTime=0;
                long Tstamp1=0;
		
		try {
			if (measureONOSTimeProp == 1) {
		            log.error("Performance: addSwitch dpid= {} Start", dpid);
			    totalStartTime = System.nanoTime();
			}
			pm.addswitch_start();
			ISwitchObject curr = op.searchSwitch(dpid);
			if (measureONOSTimeProp == 1) {
			    Tstamp1 = System.nanoTime();
		            log.error("Performance: addSwitch dpid= {} searchSwitch done at {} took {}", dpid, Tstamp1, Tstamp1-totalStartTime);
			}

			if (curr != null) {
				//If existing the switch. set The SW state ACTIVE.
				log.info("SwitchStorage:addSwitch dpid:{} already exists", dpid);
				if (measureONOSTimeProp == 1) {
				    startUpdSwitchTime = System.nanoTime();
				}
				setSwitchStateImpl(curr, SwitchState.ACTIVE);
				if (measureONOSTimeProp == 1) {
				    endUpdSwitchTime = System.nanoTime();
				}
			} else {
				if (measureONOSTimeProp == 1) {
				    startSwitchTime = System.nanoTime();
				}
				curr = addSwitchImpl(dpid);
			        pm.addswitch_end();
				if (measureONOSTimeProp == 1) {
				    endSwitchTime = System.nanoTime();
		                    //log.error("Performance: addSwitch dpid= {} addSwitchImpl done at {} took {}", dpid, endSwitchTime, endSwitchTime-startSwitchTime);
		                    log.error("Performance: addSwitch dpid= {} End searchSwitch {} addSwitchImpl {} total {} diff {}", dpid, Tstamp1-totalStartTime, endSwitchTime-startSwitchTime, endSwitchTime-totalStartTime,endSwitchTime-totalStartTime-(Tstamp1-totalStartTime)-(endSwitchTime-startSwitchTime)); 
				}
			}
			if (measureONOSTimeProp == 1) {
			    startPortTime = System.nanoTime();
			}
                        long noOfPorts = 0;
			pm.addport_start();
			for (OFPhysicalPort port: sw.getPorts()) {
				//addPort(dpid, port);
				addPortImpl(curr, port);
                                noOfPorts++;
			    	pm.addport_incr();
			}
			pm.addport_end();
			if (measureONOSTimeProp == 1) {
			    endPortTime = System.nanoTime();
			}
			// XXX for now delete devices when we change a port to prevent
			// having stale devices.
			DeviceStorageImpl deviceStorage = new DeviceStorageImpl();
			deviceStorage.init("","");
			for (IPortObject portObject : curr.getPorts()) {
				for (IDeviceObject deviceObject : portObject.getDevices()) {
					// The deviceStorage has to remove on the object gained by its own
					// FramedGraph, it can't use our objects from here
					deviceStorage.removeDeviceImpl(deviceStorage.getDeviceByMac(deviceObject.getMACAddress()));
				}
			}

			op.commit();
			if (measureONOSTimeProp == 1) {
			    totalEndTime = System.nanoTime();
			}
                        if (startSwitchTime != 0) {
                            //log.error("Performance -- switch add total time {}", endSwitchTime - startSwitchTime);
                            log.error("Performance -- switch add total time {} including_search {}", endSwitchTime - startSwitchTime, endSwitchTime - totalStartTime);
                        }
                        if (startUpdSwitchTime != 0) {
                            log.error("Performance -- switch update total time {} including_search {}", endUpdSwitchTime - startUpdSwitchTime, endUpdSwitchTime - totalStartTime);
                        }
                        if (startPortTime != 0) {
                            log.error("Performance @@ port add total time {} no of ports written {}", endPortTime - startPortTime, noOfPorts);
                        }
			if (totalStartTime != 0) {
			    log.error("Performance && total time for add switch {}", totalEndTime - totalStartTime);
			}
			success = true;
		} catch (Exception e) {
			op.rollback();
			log.error("SwitchStorage:addSwitch dpid:"+dpid+" failed", e);
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
			log.error("SwitchStorage:addSwitch dpid:"+dpid+" failed", e);
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

	@Override
	public boolean deactivateSwitch(String dpid) {
		boolean success = false;

		try {
			ISwitchObject switchObject = op.searchSwitch(dpid);
			if (switchObject != null) {
				setSwitchStateImpl(switchObject, SwitchState.INACTIVE);

				for (IPortObject portObject : switchObject.getPorts()) {
					portObject.setState("INACTIVE");
				}
				op.commit();
				success = true;
			}
			else {
				log.warn("Switch {} not found when trying to deactivate", dpid);
			}
		} catch (Exception e) {
			// TODO what type of exception is thrown when we can't commit?
			op.rollback();
			log.error("SwitchStorage:deactivateSwitch "+dpid+" failed", e);
		}

		return success;
	}

	@Override
	public boolean updatePort(String dpid, short portNum, int state, String desc) {
		boolean success = false;

		try {
			ISwitchObject sw = op.searchSwitch(dpid);

	        if (sw != null) {
	        	IPortObject p = sw.getPort(portNum);
	        	log.info("SwitchStorage:updatePort dpid:{} port:{}", dpid, portNum);
	        	if (p != null) {
	        		setPortStateImpl(p, state, desc);
				op.commit();
	        	}
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
			// TODO This is wrong. We need to make sure the port is in the
			// DB with the correct info and port state.
			return deletePort(dpid, phport.getPortNumber());
		}

		try {
			ISwitchObject sw = op.searchSwitch(dpid);

	        if (sw != null) {
	        	IPortObject portObject = addPortImpl(sw, phport);

	        	// XXX for now delete devices when we change a port to prevent
	    		// having stale devices.
	    		DeviceStorageImpl deviceStorage = new DeviceStorageImpl();
	    		deviceStorage.init("","");

	    		for (IDeviceObject deviceObject : portObject.getDevices()) {
	    			deviceStorage.removeDevice(deviceObject);
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

		DeviceStorageImpl deviceStorage = new DeviceStorageImpl();
		deviceStorage.init("","");

		try {
			ISwitchObject sw = op.searchSwitch(dpid);

	        if (sw != null) {
	        	IPortObject p = sw.getPort(port);
	            if (p != null) {
	        		log.info("SwitchStorage:deletePort dpid:{} port:{} found and set INACTIVE", dpid, port);
	        		p.setState("INACTIVE");

	        		// XXX for now delete devices when we change a port to prevent
	        		// having stale devices.
	        		for (IDeviceObject d : p.getDevices()) {
	        			deviceStorage.removeDevice(d);
	        		}
	        		op.commit();
	        	}
	        }

	        success = true;
		} catch (Exception e) {
			op.rollback();
			e.printStackTrace();
			log.error("SwitchStorage:deletePort dpid:{} port:{} failed", dpid, port);
		}

		return success;
	}

	/**
	 * Get list of all ports on the switch specified by given DPID.
	 *
	 * @param dpid DPID of desired switch.
	 * @return List of port IDs. Empty list if no port was found.
	 */
	@Override
	public List<Short> getPorts(String dpid) {
	    List<Short> ports = new ArrayList<Short>();

	    ISwitchObject srcSw = op.searchSwitch(dpid);
	    if (srcSw != null) {
		for (IPortObject srcPort : srcSw.getPorts()) {
		    ports.add(srcPort.getNumber());
		}
	    }

	    return ports;
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
					sw.getDPID(), state);
		}
	}

	private void deleteSwitchImpl(ISwitchObject sw) {
        if (sw  != null) {
        	op.removeSwitch(sw);
        	log.info("SwitchStorage:DeleteSwitchImpl dpid:{} done",
        			sw.getDPID());
        }
	}


	private IPortObject addPortImpl(ISwitchObject sw, OFPhysicalPort phport) {
		IPortObject portObject = op.searchPort(sw.getDPID(), phport.getPortNumber());

    	log.info("SwitchStorage:addPort dpid:{} port:{}",
    			sw.getDPID(), phport.getPortNumber());

    	if (portObject != null) {
    		setPortStateImpl(portObject, phport.getState(), phport.getName());
    		portObject.setState("ACTIVE");

    		// This a convoluted way of checking if the port is attached
    		// or not, but doing it this way avoids using the
    		// ISwitchObject.getPort method which uses GremlinGroovy query
    		// and takes forever.
    		boolean attached = false;
    		for (IPortObject portsOnSwitch : sw.getPorts()) {
    			if (portsOnSwitch.getPortId().equals( portObject.getPortId() )) {
    				attached = true;
    				break;
    			}
    		}

    		if (!attached) {
    			sw.addPort(portObject);
    		}

    		/*
    		if (sw.getPort(phport.getPortNumber()) == null) {
    			// The port exists but the switch has no "on" link to it
    			sw.addPort(portObject);
    		}*/

    		log.info("SwitchStorage:addPort dpid:{} port:{} exists setting as ACTIVE",
    				sw.getDPID(), phport.getPortNumber());
    	} else {
    		//addPortImpl(sw, phport.getPortNumber());
    		portObject = op.newPort(sw.getDPID(), phport.getPortNumber());
    		portObject.setState("ACTIVE");
    		setPortStateImpl(portObject, phport.getState(), phport.getName());
    		sw.addPort(portObject);
        	log.info("SwitchStorage:addPort dpid:{} port:{} done",
        			sw.getDPID(), phport.getPortNumber());
    	}

    	return portObject;
	}
	// TODO There's an issue here where a port with that ID could already
	// exist when we try to add this one (because it's left over from an
	// old topology). We need to remove an old port with the same ID when
	// we add the new port. Also it seems that old ports like this are
	// never cleaned up and will remain in the DB in the ACTIVE state forever.
	/*private IPortObject addPortImpl(ISwitchObject sw, short portNum) {
		IPortObject p = op.newPort(sw.getDPID(), portNum);
		p.setState("ACTIVE");
		sw.addPort(p);
    	log.info("SwitchStorage:addPortImpl dpid:{} port:{} done",
    			sw.getDPID(), portNum);

		return p;
	}*/

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
}
