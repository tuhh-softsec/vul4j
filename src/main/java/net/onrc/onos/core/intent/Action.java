package net.onrc.onos.core.intent;

import net.onrc.onos.core.util.FlowEntryAction;

/**
 * 
 * @author Brian O'Connor <bocon@onlab.us>
 *
 */

public abstract class Action {

    public abstract FlowEntryAction getFlowEntryAction();
}
