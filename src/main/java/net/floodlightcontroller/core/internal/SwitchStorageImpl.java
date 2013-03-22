package net.floodlightcontroller.core.internal;

import java.util.Collection;

import net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.floodlightcontroller.core.ISwitchStorage;
import net.onrc.onos.util.GraphDBConnection;
import net.onrc.onos.util.GraphDBConnection.Transaction;

import org.openflow.protocol.OFPhysicalPort;
import org.openflow.protocol.OFPhysicalPort.OFPortConfig;
import org.openflow.protocol.OFPhysicalPort.OFPortState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchStorageImpl implements ISwitchStorage {
	public GraphDBConnection conn;
	protected static Logger log = LoggerFactory.getLogger(SwitchStorageImpl.class);

	@Override
	public void update(String dpid, SwitchState state, DM_OPERATION op) {
		// TODO Auto-generated method stub
		log.info("SwitchStorage:update dpid:{} state: {} ", dpid, state);
        switch(op) {

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

	private void setStatus(String dpid, SwitchState state) {
		ISwitchObject sw = conn.utils().searchSwitch(conn, dpid);
		if (sw != null) {
			sw.setState(state.toString());
			conn.endTx(Transaction.COMMIT);
			log.info("SwitchStorage:setStatus dpid:{} state: {} done", dpid, state);
		} 	else {
			conn.endTx(Transaction.ROLLBACK);
			log.info("SwitchStorage:setStatus dpid:{} state: {} failed", dpid, state);
		}
	}

	@Override
	public void addPort(String dpid, OFPhysicalPort port) {
		// TODO Auto-generated method stub
		
        boolean portDown = ((OFPortConfig.OFPPC_PORT_DOWN.getValue() & port.getConfig()) > 0) ||
        		((OFPortState.OFPPS_LINK_DOWN.getValue() & port.getState()) > 0);
       if (portDown) {
             deletePort(dpid, port.getPortNumber());
             return;
       }
             
		try {
			ISwitchObject sw = conn.utils().searchSwitch(conn, dpid);

            if (sw != null) {
            	IPortObject p = conn.utils().searchPort(conn, dpid, port.getPortNumber());
            	log.info("SwitchStorage:addPort dpid:{} port:{}", dpid, port.getPortNumber());
            	if (p != null) {
            		log.error("SwitchStorage:addPort dpid:{} port:{} exists", dpid, port.getPortNumber());
            	} else {
            		p = conn.utils().newPort(conn);

            		p.setType("port");
            		p.setNumber(port.getPortNumber());
            		p.setState("ACTIVE");
            		p.setPortState(port.getState());
            		p.setDesc(port.getName());
            		sw.addPort(p);
            		conn.endTx(Transaction.COMMIT);
  
            	}
            }
		} catch (Exception e) {
             // TODO: handle exceptions
			conn.endTx(Transaction.ROLLBACK);
			log.error("SwitchStorage:addPort dpid:{} port:{} failed", dpid, port.getPortNumber());
		}	

	}

	@Override
	public Collection<OFPhysicalPort> getPorts(long dpid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OFPhysicalPort getPort(String dpid, short portnum) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OFPhysicalPort getPort(String dpid, String portName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addSwitch(String dpid) {
		
		log.info("SwitchStorage:addSwitch(): dpid {} ", dpid);
		
        try {
        	ISwitchObject sw = conn.utils().searchSwitch(conn, dpid);
            if (sw != null) {
                    /*
                     *  Do nothing or throw exception?
                     */
            	
            		log.info("SwitchStorage:addSwitch dpid:{} already exists", dpid);
            		sw.setState(SwitchState.ACTIVE.toString());
            		conn.endTx(Transaction.COMMIT);
            } else {
                    sw = conn.utils().newSwitch(conn);

                    sw.setType("switch");
                    sw.setDPID(dpid);
                    sw.setState(SwitchState.ACTIVE.toString());
                    conn.endTx(Transaction.COMMIT);
                    log.info("SwitchStorage:addSwitch dpid:{} added", dpid);
            }
    } catch (Exception e) {
            /*
             * retry?
             */
    	conn.endTx(Transaction.ROLLBACK);
    	log.info("SwitchStorage:addSwitch dpid:{} failed", dpid);
    }


	}

	@Override
	public void deleteSwitch(String dpid) {
		// TODO Setting inactive but we need to eventually remove data

		try {

			ISwitchObject sw = conn.utils().searchSwitch(conn, dpid);
            if (sw  != null) {
            	conn.utils().removeSwitch(conn, sw);
 
            	conn.endTx(Transaction.COMMIT);
            	log.info("SwitchStorage:DeleteSwitch dpid:{} done", dpid);
            }
		} catch (Exception e) {
             // TODO: handle exceptions
			conn.endTx(Transaction.ROLLBACK);			
			log.error("SwitchStorage:deleteSwitch {} failed", dpid);
		}

	}

	@Override
	public void deletePort(String dpid, short port) {
		// TODO Auto-generated method stub
		try {
			ISwitchObject sw = conn.utils().searchSwitch(conn, dpid);

            if (sw != null) {
            	IPortObject p = conn.utils().searchPort(conn, dpid, port);
                if (p != null) {
            		log.info("SwitchStorage:deletePort dpid:{} port:{} found and deleted", dpid, port);
            		sw.removePort(p);
            		conn.utils().removePort(conn, p);
            		conn.endTx(Transaction.COMMIT);
            	}
            }
		} catch (Exception e) {
             // TODO: handle exceptions
			conn.endTx(Transaction.ROLLBACK);
			log.info("SwitchStorage:deletePort dpid:{} port:{} failed", dpid, port);
		}	
	}

	@Override
	public void deletePort(String dpid, String portName) {
		// TODO Auto-generated method stub

	}



	@Override
	public void init(String conf) {

		conn = GraphDBConnection.getInstance(conf);
        
	}



	public void finalize() {
		close();
	}
	
	@Override
	public void close() {
		conn.close();		
	}

	
}
