package net.onrc.onos.ofcontroller.flowprogrammer.web;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.onrc.onos.ofcontroller.flowprogrammer.IFlowSyncService;

import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronizerResource extends ServerResource {
    protected final static Logger log = LoggerFactory.getLogger(SynchronizerResource.class);
    
    protected IFloodlightProviderService provider;
    protected IFlowSyncService synchronizer;

    protected boolean init() {
    	provider = (IFloodlightProviderService)
    			getContext().getAttributes().
    			get(IFloodlightProviderService.class.getCanonicalName());
    	if (provider == null) {
		    log.debug("ONOS FloodlightProvider not found");
		    return false;
		}
    	
    	synchronizer = (IFlowSyncService)
    			getContext().getAttributes().
    			get(IFlowSyncService.class.getCanonicalName());
    	if (synchronizer == null) {
		    log.debug("ONOS FlowSyncService not found");
		    return false;
		}
    	
    	return true;
    }
}
