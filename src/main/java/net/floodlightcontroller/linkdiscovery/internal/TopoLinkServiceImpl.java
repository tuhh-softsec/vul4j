package net.floodlightcontroller.linkdiscovery.internal;

import java.util.ArrayList;
import java.util.List;

import net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.floodlightcontroller.core.INetMapTopologyService.ITopoLinkService;
import net.floodlightcontroller.linkdiscovery.internal.LinkStorageImpl.ExtractLink;
import net.floodlightcontroller.routing.Link;
import net.onrc.onos.util.GraphDBConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class TopoLinkServiceImpl implements ITopoLinkService {
	
	public GraphDBConnection conn;
	protected static Logger log = LoggerFactory.getLogger(TopoLinkServiceImpl.class);


	public void finalize() {
		close();
	}
	
	@Override
	public void close() {
		conn.close();
	}
 
	@Override
	public List<Link> getActiveLinks() {
		// TODO Auto-generated method stub
		conn = GraphDBConnection.getInstance("");
		conn.close(); //Commit to ensure we see latest data
		Iterable<ISwitchObject> switches = conn.utils().getActiveSwitches(conn);
		List<Link> links = new ArrayList<Link>(); 
		for (ISwitchObject sw : switches) {
			GremlinPipeline<Vertex, Link> pipe = new GremlinPipeline<Vertex, Link>();
			ExtractLink extractor = new ExtractLink();

			pipe.start(sw.asVertex());
			pipe.enablePath(true);
			pipe.out("on").out("link").in("on").path().step(extractor);
					
			while (pipe.hasNext() ) {
				Link l = pipe.next();
				links.add(l);
			}
						
		}
		return links;
	}

	@Override
	public List<Link> getLinksOnSwitch(String dpid) {
		// TODO Auto-generated method stub
		List<Link> links = new ArrayList<Link>(); 
		ISwitchObject sw = conn.utils().searchSwitch(conn, dpid);
		GremlinPipeline<Vertex, Link> pipe = new GremlinPipeline<Vertex, Link>();
		ExtractLink extractor = new ExtractLink();

		pipe.start(sw.asVertex());
		pipe.enablePath(true);
		pipe.out("on").out("link").in("on").path().step(extractor);
			
		while (pipe.hasNext() ) {
			Link l = pipe.next();
			links.add(l);
		}
		return links;

	}
	
}
