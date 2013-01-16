package net.floodlightcontroller.linkdiscovery.internal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.floodlightcontroller.linkdiscovery.ILinkStorage;
import net.floodlightcontroller.routing.Link;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class LinkStorageImplTest extends TestCase {
	private static final String testDbLocation = "/tmp/onos-testdb";
	
	//private static final String testDbGraphML = "<?xmlversion=\"1.0\"?><graphmlxmlns=\"http://graphml.graphdrawing.org/xmlns\"><keyid=\"id\"for=\"node\"attr.name=\"id\"attr.type=\"string\"></key><keyid=\"type\"for=\"node\"attr.name=\"type\"attr.type=\"string\"></key><keyid=\"dpid\"for=\"node\"attr.name=\"dpid\"attr.type=\"string\"></key><keyid=\"desc\"for=\"node\"attr.name=\"desc\"attr.type=\"string\"></key><keyid=\"number\"for=\"node\"attr.name=\"number\"attr.type=\"int\"></key><keyid=\"dl_addr\"for=\"node\"attr.name=\"dl_addr\"attr.type=\"string\"></key><keyid=\"nw_addr\"for=\"node\"attr.name=\"nw_addr\"attr.type=\"string\"></key><keyid=\"id\"for=\"edge\"attr.name=\"id\"attr.type=\"string\"></key><keyid=\"source\"for=\"edge\"attr.name=\"source\"attr.type=\"string\"></key><keyid=\"target\"for=\"edge\"attr.name=\"target\"attr.type=\"string\"></key><keyid=\"label\"for=\"edge\"attr.name=\"label\"attr.type=\"string\"></key><graphid=\"G\"edgedefault=\"directed\"><nodeid=\"1\"><datakey=\"type\">switch</data><datakey=\"dpid\">00:00:00:00:00:00:0a:01</data><datakey=\"desc\">OpenFlowSwitchatSEA</data></node><nodeid=\"2\"><datakey=\"type\">switch</data><datakey=\"dpid\">00:00:00:00:00:00:0a:02</data><datakey=\"desc\">OpenFlowSwitchatLAX</data></node><nodeid=\"3\"><datakey=\"type\">switch</data><datakey=\"dpid\">00:00:00:00:00:00:0a:03</data><datakey=\"desc\">OpenFlowSwitchatCHI</data></node><nodeid=\"4\"><datakey=\"type\">switch</data><datakey=\"dpid\">00:00:00:00:00:00:0a:04</data><datakey=\"desc\">OpenFlowSwitchatIAH</data></node><nodeid=\"5\"><datakey=\"type\">switch</data><datakey=\"dpid\">00:00:00:00:00:00:0a:05</data><datakey=\"desc\">OpenFlowSwitchatNYC</data></node><nodeid=\"6\"><datakey=\"type\">switch</data><datakey=\"dpid\">00:00:00:00:00:00:0a:06</data><datakey=\"desc\">OpenFlowSwitchatATL</data></node><nodeid=\"100\"><datakey=\"type\">port</data><datakey=\"number\">1</data><datakey=\"desc\">port1atSEASwitch</data></node><nodeid=\"101\"><datakey=\"type\">port</data><datakey=\"number\">2</data><datakey=\"desc\">port2atSEASwitch</data></node><nodeid=\"102\"><datakey=\"type\">port</data><datakey=\"number\">3</data><datakey=\"desc\">port3atSEASwitch</data></node><nodeid=\"103\"><datakey=\"type\">port</data><datakey=\"number\">4</data><datakey=\"desc\">port4atSEASwitch</data></node><nodeid=\"104\"><datakey=\"type\">port</data><datakey=\"number\">1</data><datakey=\"desc\">port1atLAXSwitch</data></node><nodeid=\"105\"><datakey=\"type\">port</data><datakey=\"number\">2</data><datakey=\"desc\">port2atLAXSwitch</data></node><nodeid=\"106\"><datakey=\"type\">port</data><datakey=\"number\">3</data><datakey=\"desc\">port3atLAXSwitch</data></node><nodeid=\"107\"><datakey=\"type\">port</data><datakey=\"number\">1</data><datakey=\"desc\">port1atCHISwitch</data></node><nodeid=\"108\"><datakey=\"type\">port</data><datakey=\"number\">2</data><datakey=\"desc\">port2atCHISwitch</data></node><nodeid=\"109\"><datakey=\"type\">port</data><datakey=\"number\">3</data><datakey=\"desc\">port3atCHISwitch</data></node><nodeid=\"110\"><datakey=\"type\">port</data><datakey=\"number\">4</data><datakey=\"desc\">port4atCHISwitch</data></node><nodeid=\"111\"><datakey=\"type\">port</data><datakey=\"number\">1</data><datakey=\"desc\">port1atIAHSwitch</data></node><nodeid=\"112\"><datakey=\"type\">port</data><datakey=\"number\">2</data><datakey=\"desc\">port2atIAHSwitch</data></node><nodeid=\"113\"><datakey=\"type\">port</data><datakey=\"number\">3</data><datakey=\"desc\">port3atIAHSwitch</data></node><nodeid=\"114\"><datakey=\"type\">port</data><datakey=\"number\">1</data><datakey=\"desc\">port1atNYCSwitch</data></node><nodeid=\"115\"><datakey=\"type\">port</data><datakey=\"number\">2</data><datakey=\"desc\">port2atNYCSwitch</data></node><nodeid=\"116\"><datakey=\"type\">port</data><datakey=\"number\">3</data><datakey=\"desc\">port3atNYCSwitch</data></node><nodeid=\"117\"><datakey=\"type\">port</data><datakey=\"number\">1</data><datakey=\"desc\">port1atATLSwitch</data></node><nodeid=\"118\"><datakey=\"type\">port</data><datakey=\"number\">2</data><datakey=\"desc\">port2atATLSwitch</data></node><nodeid=\"119\"><datakey=\"type\">port</data><datakey=\"number\">3</data><datakey=\"desc\">port3atATLSwitch</data></node><nodeid=\"1000\"><datakey=\"type\">device</data><datakey=\"dl_addr\">20:c9:d0:4a:e1:73</data><datakey=\"nw_addr\">192.168.10.101</data></node><nodeid=\"1001\"><datakey=\"type\">device</data><datakey=\"dl_addr\">20:c9:d0:4a:e1:62</data><datakey=\"nw_addr\">192.168.20.101</data></node><nodeid=\"1002\"><datakey=\"type\">device</data><datakey=\"dl_addr\">10:40:f3:e6:8d:55</data><datakey=\"nw_addr\">192.168.10.1</data></node><nodeid=\"1003\"><datakey=\"type\">device</data><datakey=\"dl_addr\">a0:b3:cc:9c:c6:88</data><datakey=\"nw_addr\">192.168.20.1</data></node><nodeid=\"1004\"><datakey=\"type\">device</data><datakey=\"dl_addr\">00:04:20:e2:50:a2</data><datakey=\"nw_addr\">192.168.30.1</data></node><nodeid=\"1005\"><datakey=\"type\">device</data><datakey=\"dl_addr\">58:55:ca:c4:1b:a0</data><datakey=\"nw_addr\">192.168.40.1</data></node><edgeid=\"10000\"source=\"1\"target=\"101\"label=\"on\"></edge><edgeid=\"10001\"source=\"1\"target=\"102\"label=\"on\"></edge><edgeid=\"10002\"source=\"1\"target=\"103\"label=\"on\"></edge><edgeid=\"10003\"source=\"2\"target=\"104\"label=\"on\"></edge><edgeid=\"10004\"source=\"2\"target=\"105\"label=\"on\"></edge><edgeid=\"10005\"source=\"2\"target=\"106\"label=\"on\"></edge><edgeid=\"10006\"source=\"3\"target=\"107\"label=\"on\"></edge><edgeid=\"10007\"source=\"3\"target=\"108\"label=\"on\"></edge><edgeid=\"10008\"source=\"3\"target=\"109\"label=\"on\"></edge><edgeid=\"10009\"source=\"3\"target=\"110\"label=\"on\"></edge><edgeid=\"10010\"source=\"4\"target=\"111\"label=\"on\"></edge><edgeid=\"10011\"source=\"4\"target=\"112\"label=\"on\"></edge><edgeid=\"10012\"source=\"4\"target=\"113\"label=\"on\"></edge><edgeid=\"10013\"source=\"5\"target=\"114\"label=\"on\"></edge><edgeid=\"10014\"source=\"5\"target=\"115\"label=\"on\"></edge><edgeid=\"10015\"source=\"5\"target=\"116\"label=\"on\"></edge><edgeid=\"10016\"source=\"6\"target=\"117\"label=\"on\"></edge><edgeid=\"10017\"source=\"6\"target=\"118\"label=\"on\"></edge><edgeid=\"10018\"source=\"6\"target=\"119\"label=\"on\"></edge><edgeid=\"11000\"source=\"101\"target=\"107\"label=\"link\"></edge><edgeid=\"11001\"source=\"102\"target=\"104\"label=\"link\"></edge><edgeid=\"11002\"source=\"104\"target=\"102\"label=\"link\"></edge><edgeid=\"11003\"source=\"105\"target=\"111\"label=\"link\"></edge><edgeid=\"11004\"source=\"107\"target=\"101\"label=\"link\"></edge><edgeid=\"11005\"source=\"108\"target=\"112\"label=\"link\"></edge><edgeid=\"11006\"source=\"109\"target=\"114\"label=\"link\"></edge><edgeid=\"11007\"source=\"111\"target=\"105\"label=\"link\"></edge><edgeid=\"11008\"source=\"112\"target=\"108\"label=\"link\"></edge><edgeid=\"11009\"source=\"113\"target=\"117\"label=\"link\"></edge><edgeid=\"11010\"source=\"114\"target=\"109\"label=\"link\"></edge><edgeid=\"11011\"source=\"115\"target=\"118\"label=\"link\"></edge><edgeid=\"11012\"source=\"117\"target=\"113\"label=\"link\"></edge><edgeid=\"11013\"source=\"118\"target=\"115\"label=\"link\"></edge><edgeid=\"12000\"source=\"103\"target=\"1000\"label=\"host\"></edge><edgeid=\"12001\"source=\"103\"target=\"1001\"label=\"host\"></edge><edgeid=\"12002\"source=\"110\"target=\"1002\"label=\"host\"></edge><edgeid=\"12003\"source=\"116\"target=\"1003\"label=\"host\"></edge><edgeid=\"12004\"source=\"106\"target=\"1004\"label=\"host\"></edge><edgeid=\"12005\"source=\"119\"target=\"1005\"label=\"host\"></edge></graph></graphml>";
	private static final String testDbGraphML = "<?xml version=\"1.0\" ?><graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">    <key id=\"id\" for=\"node\" attr.name=\"id\" attr.type=\"string\"></key>    <key id=\"type\" for=\"node\" attr.name=\"type\" attr.type=\"string\"></key>    <key id=\"dpid\" for=\"node\" attr.name=\"dpid\" attr.type=\"string\"></key>    <key id=\"desc\" for=\"node\" attr.name=\"desc\" attr.type=\"string\"></key>    <key id=\"number\" for=\"node\" attr.name=\"number\" attr.type=\"int\"></key>    <key id=\"dl_addr\" for=\"node\" attr.name=\"dl_addr\" attr.type=\"string\"></key>    <key id=\"nw_addr\" for=\"node\" attr.name=\"nw_addr\" attr.type=\"string\"></key>    <key id=\"id\" for=\"edge\" attr.name=\"id\" attr.type=\"string\"></key>    <key id=\"source\" for=\"edge\" attr.name=\"source\" attr.type=\"string\"></key>    <key id=\"target\" for=\"edge\" attr.name=\"target\" attr.type=\"string\"></key>    <key id=\"label\" for=\"edge\" attr.name=\"label\" attr.type=\"string\"></key>    <graph id=\"G\" edgedefault=\"directed\">        <node id=\"1\">            <data key=\"type\">switch</data>            <data key=\"dpid\">00:00:00:00:00:00:0a:01</data>            <data key=\"desc\">OpenFlow Switch at SEA</data>        </node>        <node id=\"2\">            <data key=\"type\">switch</data>            <data key=\"dpid\">00:00:00:00:00:00:0a:02</data>            <data key=\"desc\">OpenFlow Switch at LAX</data>        </node>        <node id=\"3\">            <data key=\"type\">switch</data>            <data key=\"dpid\">00:00:00:00:00:00:0a:03</data>            <data key=\"desc\">OpenFlow Switch at CHI</data>        </node>        <node id=\"4\">            <data key=\"type\">switch</data>            <data key=\"dpid\">00:00:00:00:00:00:0a:04</data>            <data key=\"desc\">OpenFlow Switch at IAH</data>        </node>        <node id=\"5\">            <data key=\"type\">switch</data>            <data key=\"dpid\">00:00:00:00:00:00:0a:05</data>            <data key=\"desc\">OpenFlow Switch at NYC</data>        </node>        <node id=\"6\">            <data key=\"type\">switch</data>            <data key=\"dpid\">00:00:00:00:00:00:0a:06</data>            <data key=\"desc\">OpenFlow Switch at ATL</data>        </node>        <node id=\"100\">            <data key=\"type\">port</data>            <data key=\"number\">1</data>            <data key=\"desc\">port 1 at SEA Switch</data>        </node>        <node id=\"101\">            <data key=\"type\">port</data>            <data key=\"number\">2</data>            <data key=\"desc\">port 2 at SEA Switch</data>        </node>        <node id=\"102\">            <data key=\"type\">port</data>            <data key=\"number\">3</data>            <data key=\"desc\">port 3 at SEA Switch</data>        </node>        <node id=\"103\">            <data key=\"type\">port</data>            <data key=\"number\">4</data>            <data key=\"desc\">port 4 at SEA Switch</data>        </node>        <node id=\"104\">            <data key=\"type\">port</data>            <data key=\"number\">1</data>            <data key=\"desc\">port 1 at LAX Switch</data>        </node>        <node id=\"105\">            <data key=\"type\">port</data>            <data key=\"number\">2</data>            <data key=\"desc\">port 2 at LAX Switch</data>        </node>        <node id=\"106\">            <data key=\"type\">port</data>            <data key=\"number\">3</data>            <data key=\"desc\">port 3 at LAX Switch</data>        </node>        <node id=\"107\">            <data key=\"type\">port</data>            <data key=\"number\">1</data>            <data key=\"desc\">port 1 at CHI Switch</data>        </node>        <node id=\"108\">            <data key=\"type\">port</data>            <data key=\"number\">2</data>            <data key=\"desc\">port 2 at CHI Switch</data>        </node>        <node id=\"109\">            <data key=\"type\">port</data>            <data key=\"number\">3</data>            <data key=\"desc\">port 3 at CHI Switch</data>        </node>        <node id=\"110\">            <data key=\"type\">port</data>            <data key=\"number\">4</data>            <data key=\"desc\">port 4 at CHI Switch</data>        </node>        <node id=\"111\">            <data key=\"type\">port</data>            <data key=\"number\">1</data>            <data key=\"desc\">port 1 at IAH Switch</data>        </node>        <node id=\"112\">            <data key=\"type\">port</data>            <data key=\"number\">2</data>            <data key=\"desc\">port 2 at IAH Switch</data>        </node>        <node id=\"113\">            <data key=\"type\">port</data>            <data key=\"number\">3</data>            <data key=\"desc\">port 3 at IAH Switch</data>        </node>        <node id=\"114\">            <data key=\"type\">port</data>            <data key=\"number\">1</data>            <data key=\"desc\">port 1 at NYC Switch</data>        </node>        <node id=\"115\">            <data key=\"type\">port</data>            <data key=\"number\">2</data>            <data key=\"desc\">port 2 at NYC Switch</data>        </node>        <node id=\"116\">            <data key=\"type\">port</data>            <data key=\"number\">3</data>            <data key=\"desc\">port 3 at NYC Switch</data>        </node>        <node id=\"117\">            <data key=\"type\">port</data>            <data key=\"number\">1</data>            <data key=\"desc\">port 1 at ATL Switch</data>        </node>        <node id=\"118\">            <data key=\"type\">port</data>            <data key=\"number\">2</data>            <data key=\"desc\">port 2 at ATL Switch</data>        </node>        <node id=\"119\">            <data key=\"type\">port</data>            <data key=\"number\">3</data>            <data key=\"desc\">port 3 at ATL Switch</data>        </node>        <node id=\"1000\">            <data key=\"type\">device</data>            <data key=\"dl_addr\">20:c9:d0:4a:e1:73</data>            <data key=\"nw_addr\">192.168.10.101</data>        </node>        <node id=\"1001\">            <data key=\"type\">device</data>            <data key=\"dl_addr\">20:c9:d0:4a:e1:62</data>            <data key=\"nw_addr\">192.168.20.101</data>        </node>        <node id=\"1002\">            <data key=\"type\">device</data>            <data key=\"dl_addr\">10:40:f3:e6:8d:55</data>            <data key=\"nw_addr\">192.168.10.1</data>        </node>        <node id=\"1003\">            <data key=\"type\">device</data>            <data key=\"dl_addr\">a0:b3:cc:9c:c6:88</data>            <data key=\"nw_addr\">192.168.20.1</data>        </node>        <node id=\"1004\">            <data key=\"type\">device</data>            <data key=\"dl_addr\">00:04:20:e2:50:a2</data>            <data key=\"nw_addr\">192.168.30.1</data>        </node>        <node id=\"1005\">            <data key=\"type\">device</data>            <data key=\"dl_addr\">58:55:ca:c4:1b:a0</data>            <data key=\"nw_addr\">192.168.40.1</data>        </node>        <edge id=\"10000\" source=\"1\" target=\"101\" label=\"on\"></edge>        <edge id=\"10001\" source=\"1\" target=\"102\" label=\"on\"></edge>        <edge id=\"10002\" source=\"1\" target=\"103\" label=\"on\"></edge>        <edge id=\"10003\" source=\"2\" target=\"104\" label=\"on\"></edge>        <edge id=\"10004\" source=\"2\" target=\"105\" label=\"on\"></edge>        <edge id=\"10005\" source=\"2\" target=\"106\" label=\"on\"></edge>        <edge id=\"10006\" source=\"3\" target=\"107\" label=\"on\"></edge>        <edge id=\"10007\" source=\"3\" target=\"108\" label=\"on\"></edge>        <edge id=\"10008\" source=\"3\" target=\"109\" label=\"on\"></edge>        <edge id=\"10009\" source=\"3\" target=\"110\" label=\"on\"></edge>        <edge id=\"10010\" source=\"4\" target=\"111\" label=\"on\"></edge>        <edge id=\"10011\" source=\"4\" target=\"112\" label=\"on\"></edge>        <edge id=\"10012\" source=\"4\" target=\"113\" label=\"on\"></edge>        <edge id=\"10013\" source=\"5\" target=\"114\" label=\"on\"></edge>        <edge id=\"10014\" source=\"5\" target=\"115\" label=\"on\"></edge>        <edge id=\"10015\" source=\"5\" target=\"116\" label=\"on\"></edge>        <edge id=\"10016\" source=\"6\" target=\"117\" label=\"on\"></edge>        <edge id=\"10017\" source=\"6\" target=\"118\" label=\"on\"></edge>        <edge id=\"10018\" source=\"6\" target=\"119\" label=\"on\"></edge>        <edge id=\"11000\" source=\"101\" target=\"107\" label=\"link\"></edge>         <edge id=\"11003\" source=\"105\" target=\"111\" label=\"link\"></edge>        <edge id=\"11004\" source=\"107\" target=\"101\" label=\"link\"></edge>        <edge id=\"11005\" source=\"108\" target=\"112\" label=\"link\"></edge>        <edge id=\"11006\" source=\"109\" target=\"114\" label=\"link\"></edge>        <edge id=\"11007\" source=\"111\" target=\"105\" label=\"link\"></edge>        <edge id=\"11008\" source=\"112\" target=\"108\" label=\"link\"></edge>        <edge id=\"11009\" source=\"113\" target=\"117\" label=\"link\"></edge>        <edge id=\"11010\" source=\"114\" target=\"109\" label=\"link\"></edge>        <edge id=\"11011\" source=\"115\" target=\"118\" label=\"link\"></edge>        <edge id=\"11012\" source=\"117\" target=\"113\" label=\"link\"></edge>        <edge id=\"11013\" source=\"118\" target=\"115\" label=\"link\"></edge>        <edge id=\"12000\" source=\"103\" target=\"1000\" label=\"host\"></edge>        <edge id=\"12001\" source=\"103\" target=\"1001\" label=\"host\"></edge>        <edge id=\"12002\" source=\"110\" target=\"1002\" label=\"host\"></edge>        <edge id=\"12003\" source=\"116\" target=\"1003\" label=\"host\"></edge>        <edge id=\"12004\" source=\"106\" target=\"1004\" label=\"host\"></edge>        <edge id=\"12005\" source=\"119\" target=\"1005\" label=\"host\"></edge>      </graph>    </graphml>";
	
	private static ILinkStorage linkStorage;
	private static TitanGraph titanGraph;
	
	
	@Before
	public void setUp() {
		deleteTestDatabase();
		
		titanGraph = TitanFactory.open(testDbLocation);
		
		populateTestData();
		
		linkStorage = new MockLinkStorageImpl(titanGraph);
	}
	
	private void populateTestData(){
		Set<String> s = titanGraph.getIndexedKeys(Vertex.class);
        if (!s.contains("dpid")) {
           titanGraph.createKeyIndex("dpid", Vertex.class);
           titanGraph.stopTransaction(Conclusion.SUCCESS);
        }
        if (!s.contains("type")) {
        	titanGraph.createKeyIndex("type", Vertex.class);
        	titanGraph.stopTransaction(Conclusion.SUCCESS);
        }
        
        InputStream graphMLStream = new ByteArrayInputStream(testDbGraphML.getBytes());
        try {
			GraphMLReader.inputGraph(titanGraph, graphMLStream);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("IOException thrown");
		}
        
	}
	
	/*
	 * Add a link between port 1.102 and 2.104
	 * i.e SEA switch port 3 to LAX switch port 1
	 */
	@Test
	public void testAddSingleLink(){
		Link linkToAdd = new Link(Long.decode("0x0000000000000a01"), 3, Long.decode("0x0000000000000a02"), 1);
		
		//Use the link storage API to add the link
		linkStorage.update(linkToAdd, ILinkStorage.DM_OPERATION.INSERT);
		
		//Test if it was added correctly with the Gremlin API
		GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();
		Iterator<Vertex> it = titanGraph.getVertices("dpid", "00:00:00:00:00:00:0a:01").iterator();
		
		assertTrue(it.hasNext());
		Vertex sw1 = it.next();
		assertFalse(it.hasNext());
		
		pipe.start(sw1).out("on").has("number", 3).out("link").in("on");
		
		assertTrue(pipe.hasNext());
		Vertex sw2 = pipe.next();
		assertFalse(pipe.hasNext());
		
		//Check we ended up at the right vertex
		assertEquals((String)sw2.getProperty("dpid"), "00:00:00:00:00:00:0a:02");
	}
	
	@Test
	public void testGetLinks(){
		
	}
	
	@After
	public void tearDown() {
		//deleteTestDatabase();
	}
	
	private void deleteTestDatabase(){
		try {
			FileUtils.deleteDirectory(new File(testDbLocation));
		} catch (IOException e) {
			System.out.println("delete failed");
			e.printStackTrace();
		}
	}
}
