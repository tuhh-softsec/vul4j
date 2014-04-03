package net.onrc.onos.core.intent.runtime;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.core.intent.IntentMap;
import net.onrc.onos.core.intent.IntentOperationList;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public interface IPathCalcRuntimeService extends IFloodlightService {
    public IntentOperationList executeIntentOperations(IntentOperationList list);

    public IntentMap getHighLevelIntents();

    public IntentMap getPathIntents();

    public void purgeIntents();
}
