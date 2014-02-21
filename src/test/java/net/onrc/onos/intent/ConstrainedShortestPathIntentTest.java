package net.onrc.onos.intent;

import static org.junit.Assert.*;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class ConstrainedShortestPathIntentTest {
	NetworkGraph g;

	@Before
	public void setUp() throws Exception {
		MockNetworkGraph graph = new MockNetworkGraph();
		graph.createSampleTopology();
		g = graph;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Kryo kryo = new Kryo();
		Output output = new Output(1024);

		ConstrainedShortestPathIntent intent1 =
				new ConstrainedShortestPathIntent("1", 2L, 3L, 4L, 5L, 6L, 7L, 1000.0);

		kryo.writeObject(output, intent1);
		output.close();

		Input input = new Input(output.toBytes());
		ConstrainedShortestPathIntent intent2 =
				kryo.readObject(input, ConstrainedShortestPathIntent.class);
		input.close();

		assertEquals("1", intent2.getId());
		assertEquals(2L, intent2.getSrcSwitchDpid());
		assertEquals(3L, intent2.getSrcPortNumber());
		assertEquals(4L, intent2.getSrcMac());
		assertEquals(5L, intent2.getDstSwitchDpid());
		assertEquals(6L, intent2.getDstPortNumber());
		assertEquals(7L, intent2.getDstMac());
		assertEquals(1000.0, intent2.getBandwidth(), 0.0);
	}
}
