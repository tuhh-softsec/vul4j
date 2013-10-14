package net.onrc.onos.flow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

import net.floodlightcontroller.core.IOFSwitch;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.graph.LocalTopologyEventListener;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.ISwitchStorage.SwitchState;
import net.onrc.onos.ofcontroller.util.DataPath;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.FlowEntryAction;
import net.onrc.onos.ofcontroller.util.FlowEntryActions;
import net.onrc.onos.ofcontroller.util.FlowEntryMatch;
import net.onrc.onos.ofcontroller.util.FlowPath;
import net.onrc.onos.ofcontroller.util.Port;
import net.onrc.onos.ofcontroller.util.SwitchPort;

public class FlowManagerImpl implements IFlowManager {
	
	protected static Logger log = LoggerFactory.getLogger(LocalTopologyEventListener.class);
	protected GraphDBOperation op;

	@Override
	public void createFlow(IPortObject src_port, IPortObject dest_port) {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterable<FlowPath> getFlows(IPortObject src_port,
			IPortObject dest_port) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<FlowPath> getOutFlows(IPortObject port) {
		// TODO Auto-generated method stub
		List<FlowPath> flowPaths = new ArrayList<FlowPath> ();
		Iterable<IFlowEntry> flowEntries = port.getOutFlowEntries();

		for(IFlowEntry fe: flowEntries) {
			IFlowPath flow = fe.getFlow();
			FlowPath flowPath = new FlowPath(flow);
			flowPaths.add(flowPath);
		}
		return flowPaths;
	}

	@Override
	public void reconcileFlows(IPortObject src_port) {
		// TODO Auto-generated method stub

		log.debug("Reconcile Flows for Port removed: {}:{}",src_port.getSwitch().getDPID(),src_port.getNumber());
		Iterable<IFlowEntry> flowEntries = src_port.getOutFlowEntries();

		for(IFlowEntry fe: flowEntries) {
			IFlowPath flow = fe.getFlow();
			reconcileFlow(flow);
		}
	}

	private void reconcileFlow(IFlowPath flow) {
		// TODO Auto-generated method stub
		String src_dpid = flow.getSrcSwitch();
		String dst_dpid = flow.getDstSwitch();
		Short src_port = flow.getSrcPort();
		Short dst_port = flow.getDstPort();
		IPortObject src = null;
		IPortObject dst = null;
		src = op.searchPort(src_dpid, src_port);
		dst = op.searchPort(dst_dpid, dst_port);
		if (src != null && dst != null) {
			FlowPath newFlow = this.computeFlowPath(src,dst);
			installFlow(newFlow);
			removeFlow(flow);
		}
		
	}

	private void removeFlow(IFlowPath flow) {
		// TODO Auto-generated method stub
		
	}

	private void installFlow(FlowPath newFlow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reconcileFlow(IPortObject src_port, IPortObject dest_port) {
		// TODO Auto-generated method stub

	}

	@Override
	public FlowPath computeFlowPath(IPortObject src_port, IPortObject dest_port) {
		// TODO Auto-generated method stub
		DataPath dataPath = new DataPath(); 
		
		// FIXME: Bad idea to use FloodLight data structures (SwitchPort)
		
		dataPath.setSrcPort(new SwitchPort(new Dpid(src_port.getSwitch().getDPID()),
						new Port(src_port.getNumber())));
		dataPath.setDstPort(new SwitchPort(new Dpid(src_port.getSwitch().getDPID()),
				new Port(src_port.getNumber())));
		
		if (src_port.getSwitch().equals(dest_port.getSwitch())) {
			// on same switch create quick path
			FlowEntry flowEntry = new FlowEntry();
		    flowEntry.setDpid(new Dpid(src_port.getSwitch().getDPID()));
		    flowEntry.setInPort(new Port(src_port.getNumber()));
		    flowEntry.setOutPort(new Port(src_port.getNumber()));
		    flowEntry.setFlowEntryMatch(new FlowEntryMatch());
		    flowEntry.flowEntryMatch().enableInPort(flowEntry.inPort());

		    // Set the outgoing port output action
		    FlowEntryActions flowEntryActions = flowEntry.flowEntryActions();
		    FlowEntryAction flowEntryAction = new FlowEntryAction();
		    flowEntryAction.setActionOutput(flowEntry.outPort());
		    flowEntryActions.addAction(flowEntryAction);
		    dataPath.flowEntries().add(flowEntry);
		    
		    FlowPath flowPath = new FlowPath();
			flowPath.setDataPath(dataPath);

			return flowPath;
		}
		Vertex v_src = src_port.getSwitch().asVertex();	
		Vertex v_dest = dest_port.getSwitch().asVertex();

		//
		// Implement the Shortest Path computation by using Breath First Search
		//
		Set<Vertex> visitedSet = new HashSet<Vertex>();
		Queue<Vertex> processingList = new LinkedList<Vertex>();
		Map<Vertex, Vertex> previousVertexMap = new HashMap<Vertex, Vertex>();

		processingList.add(v_src);
		visitedSet.add(v_src);
		Boolean path_found = false;
		while (! processingList.isEmpty()) {
		    Vertex nextVertex = processingList.poll();
		    if (v_dest.equals(nextVertex)) {
			path_found = true;
			break;
		    }
		    for (Vertex parentPort : nextVertex.getVertices(Direction.OUT, "on")) {
			for (Vertex childPort : parentPort.getVertices(Direction.OUT, "link")) {
			    for (Vertex child : childPort.getVertices(Direction.IN, "on")) {
				// Ignore inactive switches
				String state = child.getProperty("state").toString();
				if (! state.equals(SwitchState.ACTIVE.toString()))
				    continue;

				if (! visitedSet.contains(child)) {
				    previousVertexMap.put(parentPort, nextVertex);
				    previousVertexMap.put(childPort, parentPort);
				    previousVertexMap.put(child, childPort);
				    visitedSet.add(child);
				    processingList.add(child);
				}
			    }
			}
		    }
		}
		if (! path_found) {
		    return null;		// No path found
		}

		List<Vertex> resultPath = new LinkedList<Vertex>();
		Vertex previousVertex = v_dest;
		resultPath.add(v_dest);
		while (! v_src.equals(previousVertex)) {
		    Vertex currentVertex = previousVertexMap.get(previousVertex);
		    resultPath.add(currentVertex);
		    previousVertex = currentVertex;
		}
		Collections.reverse(resultPath);
		
		// Loop through the result and prepare the return result
		// as a list of Flow Entries.
		//
		long nodeId = 0;
		short portId = 0;
		Port inPort = new Port(src_port.getNumber());
		Port outPort = new Port();
		int idx = 0;
		for (Vertex v: resultPath) {
		    String type = v.getProperty("type").toString();
		    // System.out.println("type: " + type);
		    if (type.equals("port")) {
			String number = v.getProperty("number").toString();
			// System.out.println("number: " + number);

			Object obj = v.getProperty("number");
			// String class_str = obj.getClass().toString();
			if (obj instanceof Short) {
			    portId = (Short)obj;
			} else if (obj instanceof Integer) {
			    Integer int_nodeId = (Integer)obj;
			    portId = int_nodeId.shortValue();
			    // int int_nodeId = (Integer)obj;
			    // portId = (short)int_nodeId.;
			}
		    } else if (type.equals("switch")) {
			String dpid = v.getProperty("dpid").toString();
			nodeId = HexString.toLong(dpid);

			// System.out.println("dpid: " + dpid);
		    }
		    idx++;
		    if (idx == 1) {
			continue;
		    }
		    int mod = idx % 3;
		    if (mod == 0) {
			// Setup the incoming port
			inPort = new Port(portId);
			continue;
		    }
		    if (mod == 2) {
			// Setup the outgoing port, and add the Flow Entry
			outPort = new Port(portId);

			FlowEntry flowEntry = new FlowEntry();
			flowEntry.setDpid(new Dpid(nodeId));
			flowEntry.setInPort(inPort);
			flowEntry.setOutPort(outPort);
			flowEntry.setFlowEntryMatch(new FlowEntryMatch());
		    flowEntry.flowEntryMatch().enableInPort(flowEntry.inPort());
		    
		    // Set the outgoing port output action
		    FlowEntryActions flowEntryActions = flowEntry.flowEntryActions();
		    FlowEntryAction flowEntryAction = new FlowEntryAction();
		    flowEntryAction.setActionOutput(flowEntry.outPort());
		    flowEntryActions.addAction(flowEntryAction);
		    dataPath.flowEntries().add(flowEntry);
			continue;
		    }
		}
		if (idx > 0) {
		    // Add the last Flow Entry
		    FlowEntry flowEntry = new FlowEntry();
		    flowEntry.setDpid(new Dpid(nodeId));
		    flowEntry.setInPort(inPort);
		    flowEntry.setOutPort(new Port(dest_port.getNumber()));
		    flowEntry.setFlowEntryMatch(new FlowEntryMatch());
		    flowEntry.flowEntryMatch().enableInPort(flowEntry.inPort());
		    
		    // Set the outgoing port output action
		    FlowEntryActions flowEntryActions = flowEntry.flowEntryActions();
		    FlowEntryAction flowEntryAction = new FlowEntryAction();
		    flowEntryAction.setActionOutput(flowEntry.outPort());
		    flowEntryActions.addAction(flowEntryAction);
		    dataPath.flowEntries().add(flowEntry);
		    dataPath.flowEntries().add(flowEntry);
		}

	
		if (dataPath.flowEntries().size() > 0) {
		    FlowPath flowPath = new FlowPath();
			flowPath.setDataPath(dataPath);

			return flowPath;
		}
		return null;		
		
	}

	@Override
	public Iterable<FlowEntry> getFlowEntries(FlowPath flow) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean installRemoteFlowEntry(FlowPath flowPath,
					      FlowEntry entry) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeRemoteFlowEntry(FlowPath flowPath,
					     FlowEntry entry) {
		return false;
		// TODO Auto-generated method stub

	}

	@Override
	public boolean installFlowEntry(IOFSwitch mySwitch,
					FlowPath flowPath,
					FlowEntry flowEntry) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeFlowEntry(IOFSwitch mySwitch,
				       FlowPath flowPath,
				       FlowEntry flowEntry) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
