package net.onrc.onos.intent;

import static org.junit.Assert.assertEquals;
import net.onrc.onos.ofcontroller.networkgraph.LinkEvent;
import net.onrc.onos.ofcontroller.networkgraph.Path;
import net.onrc.onos.ofcontroller.util.serializers.KryoFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class IntentOperationListTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		IntentOperationList opList = new IntentOperationList();

		ConstrainedShortestPathIntent cspIntent1 =
				new ConstrainedShortestPathIntent("1", 2L, 3L, 4L, 5L, 6L, 7L, 1000.0);

		Path path = new Path();
		path.add(new LinkEvent(1L, 2L, 3L, 4L));
		path.add(new LinkEvent(5L, 6L, 7L, 8L));
		path.add(new LinkEvent(9L, 0L, 1L, 2L));

		PathIntent pathIntent1 = new PathIntent("11", path, 123.45, cspIntent1);
		opList.add(IntentOperation.Operator.ADD, pathIntent1);
		opList.add(IntentOperation.Operator.REMOVE, new Intent("22"));

		KryoFactory factory = new KryoFactory();
		Kryo kryo = factory.newKryo();
		Output output = new Output(1024);
		kryo.writeObject(output, opList);
		output.close();

		byte[] bytes = output.toBytes();

		Input input = new Input(bytes);
		IntentOperationList rcvOpList = kryo.readObject(input, IntentOperationList.class);

		assertEquals(2, rcvOpList.size());

		IntentOperation op1 = rcvOpList.get(0);
		IntentOperation op2 = rcvOpList.get(1);

		assertEquals(IntentOperation.Operator.ADD, op1.operator);
		PathIntent intent1 = (PathIntent) op1.intent;
		assertEquals("11", intent1.getId());
		assertEquals(3, intent1.getPath().size());

		assertEquals(IntentOperation.Operator.REMOVE, op2.operator);
		Intent intent2 = op2.intent;
		assertEquals("22", intent2.getId());
	}
}
