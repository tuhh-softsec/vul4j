package net.onrc.onos.intent;

import java.util.HashSet;
import java.util.Set;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.intent.IntentOperation.Operator;
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
	protected long sw;
	protected Match match;
	protected Set<Action> actions;
	protected Operator operator;
	
	public FlowEntry(long sw, long srcPort, long dstPort, 
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
	
	public long getSwitch() {
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
		entry.setDpid(new Dpid(sw));
		entry.setFlowEntryId(new FlowEntryId(hashCode())); // naive, but useful for now
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
		default:
		    break;
		}
		return entry;
	}
	
	
	public int hashCode() {
	    return match.hashCode();
	}
	
	public boolean equals(Object o) {
	    if(!(o instanceof FlowEntry)) {
		return false;
	    }
	    FlowEntry other = (FlowEntry) o;
	    // Note: we should not consider the operator for this comparison
	    return this.match.equals(other.match) 
		&& this.actions.containsAll(other.actions)
		&& other.actions.containsAll(this.actions);
	}
}
