package net.floodlightcontroller.linkdiscovery.internal;

import java.util.List;

import net.floodlightcontroller.core.INetMapTopologyService.ITopoLinkService;
import net.floodlightcontroller.routing.Link;

public class TopoLinkServiceImpl implements ITopoLinkService {
	
	ThreadLocal<LinkStorageImpl> store = new ThreadLocal<LinkStorageImpl>() {
		@Override
		protected LinkStorageImpl initialValue() {
			LinkStorageImpl inStore = new LinkStorageImpl();
			//TODO: Get the file path from global properties
			inStore.init("/tmp/cassandra.titan");
			return inStore;
		}
	};
 
	LinkStorageImpl linkStore = store.get();
	@Override
	public List<Link> getActiveLinks() {
		// TODO Auto-generated method stub
		List<Link> retval = linkStore.getActiveLinks();
		return retval;
	}

	@Override
	public List<Link> getLinksOnSwitch(String dpid) {
		// TODO Auto-generated method stub
		return linkStore.getLinks(dpid);
	}
}
