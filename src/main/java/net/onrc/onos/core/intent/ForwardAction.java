package net.onrc.onos.core.intent;

import net.onrc.onos.core.util.FlowEntryAction;

/**
 * @author Brian O'Connor <bocon@onlab.us>
 */

class ForwardAction extends Action {
    protected long dstPort;

    public ForwardAction(long dstPort) {
        this.dstPort = dstPort;
    }

    public String toString() {
        return Long.toString(dstPort);
    }

    @Override
    public FlowEntryAction getFlowEntryAction() {
        FlowEntryAction action = new FlowEntryAction();
        action.setActionOutput(new net.onrc.onos.core.util.Port((short) dstPort));
        return action;
    }

    public int hashCode() {
        return (int) dstPort;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ForwardAction)) {
            return false;
        }
        ForwardAction action = (ForwardAction) o;
        return this.dstPort == action.dstPort;
    }
}
