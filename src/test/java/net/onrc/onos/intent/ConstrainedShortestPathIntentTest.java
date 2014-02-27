package net.onrc.onos.intent;

import static org.junit.Assert.assertEquals;
import net.onrc.onos.ofcontroller.util.serializers.KryoFactory;

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
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreate() {
		ConstrainedShortestPathIntent intent1 =
				new ConstrainedShortestPathIntent("1", 2L, 3L, 4L, 5L, 6L, 7L, 1000.0);

		assertEquals("1", intent1.getId());
		assertEquals(2L, intent1.getSrcSwitchDpid());
		assertEquals(3L, intent1.getSrcPortNumber());
		assertEquals(4L, intent1.getSrcMac());
		assertEquals(5L, intent1.getDstSwitchDpid());
		assertEquals(6L, intent1.getDstPortNumber());
		assertEquals(7L, intent1.getDstMac());
		assertEquals(1000.0, intent1.getBandwidth(), 0.0);
	}

	@Test
	public void testKryo() {
		KryoFactory factory = new KryoFactory();
		Kryo kryo = factory.newKryo();
		Output output = new Output(1000);

		ConstrainedShortestPathIntent intent1 =
				new ConstrainedShortestPathIntent("1", 2L, 3L, 4L, 5L, 6L, 7L, 1000.0);
		kryo.writeObject(output, intent1);

		output.close();
		byte bytes[] = output.toBytes();

		Input input = new Input(bytes);
		ConstrainedShortestPathIntent intent2 = kryo.readObject(input, ConstrainedShortestPathIntent.class);
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
