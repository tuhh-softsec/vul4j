package net.onrc.onos.intent;

import java.util.HashSet;
import java.util.Set;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.intent.IntentOperation.Operator;
import net.onrc.onos.ofcontroller.networkgraph.Port;
import net.onrc.onos.ofcontroller.networkgraph.Switch;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.FlowEntryActions;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowEntryUserState;

/**
 * 
 * @author Brian O'Connor <bocon@onlab.us>
 *
 */

public class FlowEntry {
	protected Switch sw;
	protected Match match;
	protected Set<Action> actions;
	protected Operator operator;
	
	public FlowEntry(Switch sw, Port srcPort, Port dstPort, 
			 MACAddress srcMac, MACAddress dstMac,
			 Operator operator) {
		this.sw = sw;
		this.match = new Match(sw, srcPort, srcMac, dstMac);
		this.actions = new HashSet<Action>();
		this.actions.add(new ForwardAction(dstPort));
		this.operator = operator;
	}
	
	public String toString() {
		return match + "->" + actions;
	}
	
	public Switch getSwitch() {
	    return sw;
	}
	
	public Operator getOperator() {
	    return operator;
	}
	
	public void setOperator(Operator op) {
	    operator = op;
	}
	
	public net.onrc.onos.ofcontroller.util.FlowEntry getFlowEntry() {
		net.onrc.onos.ofcontroller.util.FlowEntry entry = new net.onrc.onos.ofcontroller.util.FlowEntry();
		entry.setDpid(new Dpid(sw.getDpid()));
		entry.setFlowEntryId(new FlowEntryId(0)); // all zero for now
		entry.setFlowEntryMatch(match.getFlowEntryMatch());
		FlowEntryActions flowEntryActions = new FlowEntryActions();
		for(Action action : actions) {
		    flowEntryActions.addAction(action.getFlowEntryAction());
		}
		entry.setFlowEntryActions(flowEntryActions);
		switch(operator) {
		case ADD:
		    entry.setFlowEntryUserState(FlowEntryUserState.FE_USER_MODIFY);
		    break;
		case REMOVE:
		    entry.setFlowEntryUserState(FlowEntryUserState.FE_USER_DELETE);
		    break;
		}
		return entry;
	}
	
	//TODO: implement hash for cookie
	//TODO: implement equals (don't include operator!)
}