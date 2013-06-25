package net.onrc.onos.ofcontroller.core.internal;

import java.util.ArrayList;
import java.util.List;

import net.floodlightcontroller.routing.Link;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyService.ITopoLinkService;
import net.onrc.onos.ofcontroller.core.internal.LinkStorageImpl.ExtractLink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class TopoLinkServiceImpl implements ITopoLinkService {
	
	protected GraphDBOperation op;
	protected static Logger log = LoggerFactory.getLogger(TopoLinkServiceImpl.class);

	public void finalize() {
		close();
	}
	
	@Override
	public void close() {
		op.close();
	}
 
	@Override
	public List<Link> getActiveLinks() {
		// TODO Auto-generated method stub
		op = new GraphDBOperation("");
		op.commit(); //Commit to ensure we see latest data
		Iterable<ISwitchObject> switches = op.getActiveSwitches();
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
		op.commit();
		return links;
	}

	@Override
	public List<Link> getLinksOnSwitch(String dpid) {
		// TODO Auto-generated method stub
		List<Link> links = new ArrayList<Link>(); 
		ISwitchObject sw = op.searchSwitch(dpid);
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
