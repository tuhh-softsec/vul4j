package net.floodlightcontroller.core.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openflow.protocol.OFPhysicalPort;
import org.openflow.protocol.OFPhysicalPort.OFPortConfig;
import org.openflow.protocol.OFPhysicalPort.OFPortState;

import com.thinkaurelius.titan.core.TitanException;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.gremlin.java.GremlinPipeline;

import net.floodlightcontroller.core.ISwitchStorage;

public class SwitchStorageImpl implements ISwitchStorage {
	public TitanGraph graph;
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
		Vertex sw;
		try {
            if ((sw = graph.getVertices("dpid",dpid).iterator().next()) != null) {
            	sw.setProperty("state",state.toString());
            	graph.stopTransaction(Conclusion.SUCCESS);
            	log.info("SwitchStorage:setStatus dpid:{} state: {} done", dpid, state);
            }
		} catch (TitanException e) {
             // TODO: handle exceptions
			log.info("SwitchStorage:setStatus dpid:{} state: {} failed", dpid, state);
		}
            	
		
	}

	@Override
	public void addPort(String dpid, OFPhysicalPort port) {
		// TODO Auto-generated method stub
		Vertex sw;
		
        boolean portDown = ((OFPortConfig.OFPPC_PORT_DOWN.getValue() & port.getConfig()) > 0) ||
        		((OFPortState.OFPPS_LINK_DOWN.getValue() & port.getState()) > 0);
       if (portDown) {
             deletePort(dpid, port.getPortNumber());
             return;
       }
		try {
            if ((sw = graph.getVertices("dpid",dpid).iterator().next()) != null) {
            	log.info("SwitchStorage:addPort dpid:{} port:{}", dpid, port.getPortNumber());
            	// TODO: Check if port exists
            	if (sw.query().direction(Direction.OUT).labels("on").has("number",port.getPortNumber()).vertices().iterator().hasNext()) {
            		//TODO: Do nothing for now
            		log.error("SwitchStorage:addPort dpid:{} port:{} exists", dpid, port.getPortNumber());
            	} else {
            		Vertex p = graph.addVertex(null);
            		p.setProperty("type","port");
            		p.setProperty("number",port.getPortNumber());
            		p.setProperty("state",port.getState());
            		p.setProperty("desc",port.getName());
            		Edge e = graph.addEdge(null, sw, p, "on");
            		e.setProperty("state","ACTIVE");
            		e.setProperty("number", port.getPortNumber());
                     	
            		graph.stopTransaction(Conclusion.SUCCESS);
            	}
            }
		} catch (TitanException e) {
             // TODO: handle exceptions
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
            if (graph.getVertices("dpid",dpid).iterator().hasNext()) {
                    /*
                     *  Do nothing or throw exception?
                     */
            		Vertex sw = graph.getVertices("dpid",dpid).iterator().next();
            	
            		log.info("SwitchStorage:addSwitch dpid:{} already exists", dpid);
            		sw.setProperty("state",SwitchState.ACTIVE.toString());
            		graph.stopTransaction(Conclusion.SUCCESS);
            } else {
                    Vertex sw = graph.addVertex(null);

                    sw.setProperty("type","switch");
                    sw.setProperty("dpid", dpid);
                    sw.setProperty("state",SwitchState.ACTIVE.toString());
                    graph.stopTransaction(Conclusion.SUCCESS);
                    log.info("SwitchStorage:addSwitch dpid:{} added", dpid);
            }
    } catch (TitanException e) {
            /*
             * retry till we succeed?
             */
    	log.info("SwitchStorage:addSwitch dpid:{} failed", dpid);
    }


	}

	@Override
	public void deleteSwitch(String dpid) {
		// TODO Setting inactive but we need to eventually remove data
		Vertex sw;
		try {
			
            if ((sw = graph.getVertices("dpid",dpid).iterator().next()) != null) {
            	graph.removeVertex(sw);
            	graph.stopTransaction(Conclusion.SUCCESS);
            	log.info("SwitchStorage:DeleteSwitch dpid:{} done", dpid);
            }
		} catch (TitanException e) {
             // TODO: handle exceptions
			log.error("SwitchStorage:deleteSwitch {} failed", dpid);
		}

	}

	@Override
	public void deletePort(String dpid, short port) {
		// TODO Auto-generated method stub
		Vertex sw;
		try {
            if ((sw = graph.getVertices("dpid",dpid).iterator().next()) != null) {
            	// TODO: Check if port exists
            	log.info("SwitchStorage:deletePort dpid:{} port:{}", dpid, port);
            	if (sw.query().direction(Direction.OUT).labels("on").has("number",port).vertices().iterator().hasNext()) {
            		Vertex p = sw.query().direction(Direction.OUT).labels("on").has("number",port).vertices().iterator().next();
            		log.info("SwitchStorage:deletePort dpid:{} port:{} found and deleted", dpid, port);
            		graph.removeVertex(p);
            		graph.stopTransaction(Conclusion.SUCCESS);
            	}
            }
		} catch (TitanException e) {
             // TODO: handle exceptions
			log.info("SwitchStorage:deletePort dpid:{} port:{} failed", dpid, port);
		}	
	}

	@Override
	public void deletePort(String dpid, String portName) {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterable<ISwitchObject> getActiveSwitches() {
		// TODO Add unit test
		FramedGraph<TitanGraph> fg = new FramedGraph<TitanGraph>(graph);
		Iterable<ISwitchObject> switches =  fg.getVertices("type","switch",ISwitchObject.class);
		List<ISwitchObject> activeSwitches = new ArrayList<ISwitchObject>();

		for (ISwitchObject sw: switches) {
			if(sw.getState().equals(SwitchState.ACTIVE.toString())) {
				activeSwitches.add(sw);
			}
		}

		return activeSwitches;		
	}

	@Override
	public void init(String conf) {

        graph = TitanFactory.open(conf);
        
        // FIXME: Creation on Indexes should be done only once
        Set<String> s = graph.getIndexedKeys(Vertex.class);
        if (!s.contains("dpid")) {
           graph.createKeyIndex("dpid", Vertex.class);
           graph.stopTransaction(Conclusion.SUCCESS);
        }
        if (!s.contains("type")) {
        	graph.createKeyIndex("type", Vertex.class);
        	graph.stopTransaction(Conclusion.SUCCESS);
        }
	}

	@Override
	public Iterable<ISwitchObject> getAllSwitches() {
		// TODO Auto-generated method stub
		FramedGraph<TitanGraph> fg = new FramedGraph<TitanGraph>(graph);
		Iterable<ISwitchObject> switches =  fg.getVertices("type","switch",ISwitchObject.class);

		for (ISwitchObject sw: switches) {
			log.debug("switch: {}", sw.getDPID());
		}

		return switches;
	}

	@Override
	public Iterable<ISwitchObject> getInactiveSwitches() {
		// TODO Auto-generated method stub
		FramedGraph<TitanGraph> fg = new FramedGraph<TitanGraph>(graph);
		Iterable<ISwitchObject> switches =  fg.getVertices("type","switch",ISwitchObject.class);

		List<ISwitchObject> inactiveSwitches = new ArrayList<ISwitchObject>();
		
		for (ISwitchObject sw: switches) {
			if(sw.getState().equals(SwitchState.INACTIVE.toString())) {
				inactiveSwitches.add(sw);
			}
		}
		return inactiveSwitches;
	}

	public void finalize() {
		close();
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		graph.shutdown();
		
	}

	
}
