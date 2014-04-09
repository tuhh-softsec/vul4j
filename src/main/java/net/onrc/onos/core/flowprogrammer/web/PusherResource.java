package net.onrc.onos.core.flowprogrammer.web;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.onrc.onos.core.flowprogrammer.IFlowPusherService;

import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PusherResource extends ServerResource {
    protected static final Logger log = LoggerFactory.getLogger(PusherResource.class);

    protected IFloodlightProviderService provider;
    protected IFlowPusherService pusher;

    protected boolean init() {
        provider = (IFloodlightProviderService)
                getContext().getAttributes().
                        get(IFloodlightProviderService.class.getCanonicalName());
        if (provider == null) {
            log.debug("ONOS FloodlightProvider not found");
            return false;
        }

        pusher = (IFlowPusherService) getContext().getAttributes().
                get(IFlowPusherService.class.getCanonicalName());
        if (pusher == null) {
            log.debug("ONOS FlowPusherService not found");
            return false;
        }
        return true;
    }
}
