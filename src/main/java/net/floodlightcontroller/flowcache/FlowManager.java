package net.floodlightcontroller.flowcache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.flowcache.IFlowService;
import net.floodlightcontroller.flowcache.web.FlowWebRoutable;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.util.CallerId;
import net.floodlightcontroller.util.DataPathEndpoints;
import net.floodlightcontroller.util.FlowId;
import net.floodlightcontroller.util.FlowPath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowManager implements IFloodlightModule, IFlowService {

    protected IRestApiService restApi;

    /** The logger. */
    private static Logger logger =
	LoggerFactory.getLogger(FlowManager.class);

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        Collection<Class<? extends IFloodlightService>> l = 
            new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFlowService.class);
        return l;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> 
			       getServiceImpls() {
        Map<Class<? extends IFloodlightService>,
        IFloodlightService> m = 
            new HashMap<Class<? extends IFloodlightService>,
                IFloodlightService>();
        m.put(IFlowService.class, this);
        return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> 
                                                    getModuleDependencies() {
	Collection<Class<? extends IFloodlightService>> l =
	    new ArrayList<Class<? extends IFloodlightService>>();
	l.add(IRestApiService.class);
        return l;
    }

    @Override
    public void init(FloodlightModuleContext context)
	throws FloodlightModuleException {
	restApi = context.getServiceImpl(IRestApiService.class);
    }

    @Override
    public void startUp(FloodlightModuleContext context) {
	restApi.addRestletRoutable(new FlowWebRoutable());
    }

    /**
     * Add a flow.
     *
     * Internally, ONOS will automatically register the installer for
     * receiving Flow Path Notifications for that path.
     *
     * @param flowPath the Flow Path to install.
     * @param flowId the return-by-reference Flow ID as assigned internally.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean addFlow(FlowPath flowPath, FlowId flowId) {
	// TODO
	return true;
    }

    /**
     * Delete a previously added flow.
     *
     * @param flowId the Flow ID of the flow to delete.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean deleteFlow(FlowId flowId) {
	// TODO
	return true;
    }

    /**
     * Get a previously added flow.
     *
     * @param flowId the Flow ID of the flow to get.
     * @param flowPath the return-by-reference flow path.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean getFlow(FlowId flowId, FlowPath flowPath) {
	// TODO
	return true;
    }

    /**
     * Get a previously added flow by a specific installer for given
     * data path endpoints.
     *
     * @param installerId the Caller ID of the installer of the flow to get.
     * @param dataPathEndpoints the data path endpoints of the flow to get.
     * @param flowPath the return-by-reference flow path.
     * @return true on success, otherwise false.
     */
    @Override
    public boolean getFlow(CallerId installerId,
			   DataPathEndpoints dataPathEndpoints,
			   FlowPath flowPath) {
	// TODO
	return true;
    }

    /**
     * Get all installed flows by all installers for given data path endpoints.
     *
     * @param dataPathEndpoints the data path endpoints of the flows to get.
     * @param flowPaths the return-by-reference list of flows.
     */
    @Override
    public void getAllFlows(DataPathEndpoints dataPathEndpoints,
			    ArrayList<FlowPath> flowPaths) {
	// TODO
    }

    /**
     * Get all installed flows by all installers.
     *
     * @param flowPaths the return-by-reference list of flows.
     */
    @Override
    public void getAllFlows(ArrayList<FlowPath> flowPaths) {
	// TODO
    }
}
