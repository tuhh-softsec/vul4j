package net.onrc.onos.intent;

import static org.junit.Assert.assertEquals;
import net.onrc.onos.intent.Intent.IntentState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IntentMapTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		IntentMap intents = new IntentMap();
		IntentOperationList operations = new IntentOperationList();

		// add three intents

		ShortestPathIntent intent1 =
				new ShortestPathIntent("1", 11L, 12L, 13L, 14L, 15L, 16L);
		ShortestPathIntent intent2 =
				new ShortestPathIntent("2", 21L, 22L, 23L, 24L, 25L, 26L);
		ConstrainedShortestPathIntent intent3 =
				new ConstrainedShortestPathIntent("3", 31L, 32L, 33L, 34L, 35L, 36L, 1000.0);

		operations.add(new IntentOperation(IntentOperation.Operator.ADD, intent1));
		operations.add(new IntentOperation(IntentOperation.Operator.ADD, intent2));
		operations.add(new IntentOperation(IntentOperation.Operator.ADD, intent3));
		intents.executeOperations(operations);

		// check

		assertEquals(3, intents.getAllIntents().size());
		assertEquals(intent1, intents.getIntent("1"));
		assertEquals(intent2, intents.getIntent("2"));
		assertEquals(intent3, intents.getIntent("3"));

		// request removal of an intent

		Intent intent4 = new Intent("1");
		operations.clear();
		operations.add(new IntentOperation(IntentOperation.Operator.REMOVE, intent4));
		intents.executeOperations(operations);

		// check

		assertEquals(3, intents.getAllIntents().size());
		assertEquals(IntentState.DEL_REQ, intent1.getState());

		// change intents' state which will be purged 

		intent2.setState(IntentState.INST_NACK);
		intent3.setState(IntentState.DEL_ACK);

		// purge

		intents.purge();

		// check

		assertEquals(1, intents.getAllIntents().size());
		assertEquals("1", intents.getAllIntents().iterator().next().getId());
	}
}
