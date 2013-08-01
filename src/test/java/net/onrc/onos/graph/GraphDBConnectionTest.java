/**
 * 
 */
package net.onrc.onos.graph;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.*;

import java.util.*;

import net.onrc.onos.graph.GraphDBOperation;

import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.event.EventTransactionalGraph;
import com.tinkerpop.frames.FramedGraph;

/**
 * @author Toshio Koide
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({
	GraphDBConnection.class,
	GraphDBOperation.class,
	TitanFactory.class,
	EventTransactionalGraph.class})
public class GraphDBConnectionTest {
	private static TitanGraph graph = null;
	private static EventTransactionalGraph<TitanGraph> eg = null;
	private static Boolean isGraphOpen = false;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	
	private void expectDBConnectionAvailable() throws Exception {
		isGraphOpen = false;
		
		// create mock objects
		mockStatic(TitanFactory.class);
		mockStatic(EventTransactionalGraph.class);
		graph = createMock(TitanGraph.class);
		eg = createMock(EventTransactionalGraph.class);
		
		// setup expectations
		expect(graph.isOpen()).andAnswer(new IAnswer<Boolean>() {
			@Override
			public Boolean answer() throws Throwable {
				return isGraphOpen;
			}
		}).anyTimes();
		expect(TitanFactory.open("/path/to/dummy")).andAnswer(new IAnswer<TitanGraph>() {
			@Override
			public TitanGraph answer() throws Throwable {
				isGraphOpen = true;
				return graph;
			}
		}).anyTimes();
		expect(graph.getIndexedKeys(Vertex.class)).andReturn(new TreeSet<String>());
		graph.createKeyIndex("dpid", Vertex.class);
		graph.createKeyIndex("port_id", Vertex.class);
		graph.createKeyIndex("type", Vertex.class);
		graph.createKeyIndex("dl_addr", Vertex.class);
		graph.createKeyIndex("flow_id", Vertex.class);
		graph.createKeyIndex("flow_entry_id", Vertex.class);
		graph.createKeyIndex("switch_state", Vertex.class);
		graph.commit();
		expectNew(EventTransactionalGraph.class, graph).andReturn(eg);
	}
	
	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBConnection#getInstance(java.lang.String)}.
	 * @throws Exception
	 */
	@Test
	public final void testGetInstance() throws Exception {
		// setup expectations
		expectDBConnectionAvailable();
		
		// start the test
		replayAll();
		GraphDBConnection conn = GraphDBConnection.getInstance("/path/to/dummy");

		// verify the test
		verifyAll();
		assertNotNull(conn);
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBConnection#getFramedGraph()}.
	 * @throws Exception
	 */
	@Test
	public final void testGetFramedGraph() throws Exception {
		// setup expectations
		expectDBConnectionAvailable();
		
		// start the test
		replayAll();
		GraphDBConnection conn = GraphDBConnection.getInstance("/path/to/dummy");
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		
		// verify the test
		verifyAll();
		assertNotNull(fg);
		assertEquals(graph, fg.getBaseGraph());
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBConnection#addEventListener(net.onrc.onos.graph.LocalGraphChangedListener)}.
	 * @throws Exception 
	 */
	@Test
	public final void testAddEventListener() throws Exception {
		// instantiate required objects
		LocalGraphChangedListener listener = new LocalTopologyEventListener(null);

		// setup expectations
		expectDBConnectionAvailable();
		eg.addListener(listener);
		
		// start the test
		replayAll();
		GraphDBConnection conn = GraphDBConnection.getInstance("/path/to/dummy");
		conn.addEventListener(listener);
		
		// verify the test
		verifyAll();
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBConnection#isValid()}.
	 * @throws Exception 
	 */
	@Test
	public final void testIsValid() throws Exception {
		// setup expectations
		expectDBConnectionAvailable();

		// start the test
		replayAll();
		GraphDBConnection conn = GraphDBConnection.getInstance("/path/to/dummy");
		Boolean result = conn.isValid();
		
		// verify the test
		verifyAll();
		assertTrue(result);
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBConnection#commit()}.
	 * @throws Exception 
	 */
	@Test
	public final void testCommit() throws Exception {
		// setup expectations
		expectDBConnectionAvailable();
		graph.commit();
		
		// start the test
		replayAll();
		GraphDBConnection conn = GraphDBConnection.getInstance("/path/to/dummy");
		conn.commit();

		// verify the test
		verifyAll();
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBConnection#rollback()}.
	 * @throws Exception 
	 */
	@Test
	public final void testRollback() throws Exception {
		// setup expectations
		expectDBConnectionAvailable();
		graph.rollback();
		
		// start the test
		replayAll();
		GraphDBConnection conn = GraphDBConnection.getInstance("/path/to/dummy");
		conn.rollback();

		// verify the test
		verifyAll();
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBConnection#close()}.
	 * @throws Exception 
	 */
	@Test
	public final void testClose() throws Exception {
		// setup expectations
		expectDBConnectionAvailable();
		graph.commit();
		
		// start the test
		replayAll();
		GraphDBConnection conn = GraphDBConnection.getInstance("/path/to/dummy");
		conn.close();

		// verify the test
		verifyAll();
	}

}
