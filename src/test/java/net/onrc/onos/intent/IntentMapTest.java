package net.onrc.onos.intent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import net.onrc.onos.intent.ErrorIntent.ErrorType;
import net.onrc.onos.intent.Intent.IntentState;
import net.onrc.onos.intent.IntentMap.ChangedEventType;
import net.onrc.onos.intent.IntentOperation.Operator;
import net.onrc.onos.intent.runtime.IntentStateList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class IntentMapTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreate() {
		IntentMap intents = new IntentMap();
		assertEquals(0, intents.getAllIntents().size());
	}

	@Test
	public void testChangedEventCreate() {
		IntentMap intents = new IntentMap();
		IntentMap.ChangedEvent event = intents.new ChangedEvent(
				ChangedEventType.ADDED,
				new Intent("id1"));
		assertEquals(ChangedEventType.ADDED, event.eventType);
		assertEquals("id1", event.intent.getId());
	}

	@Test
	public void testAddOperations() {
		IntentMap intents = new IntentMap();
		assertEquals(0, intents.getAllIntents().size());

		Intent intent1 = new Intent("1");
		ShortestPathIntent intent2 =
				new ShortestPathIntent("2", 21L, 22L, 23L, 24L, 25L, 26L);
		ConstrainedShortestPathIntent intent3 =
				new ConstrainedShortestPathIntent("3", 31L, 32L, 33L, 34L, 35L, 36L, 1000.0);

		IntentOperationList operations = new IntentOperationList();
		operations.add(Operator.ADD, intent1);
		operations.add(Operator.ADD, intent2);
		operations.add(Operator.ADD, intent3);
		assertEquals(3, operations.size());

		intents.executeOperations(operations);
		assertEquals(3, intents.getAllIntents().size());
		assertSame(intent1, intents.getIntent("1"));
		assertSame(intent2, intents.getIntent("2"));
		assertSame(intent3, intents.getIntent("3"));
	}

	@Test
	public void testAddOperationsOverwrite() {
		IntentMap intents = new IntentMap();

		Intent intent1 = new Intent("1");
		Intent intent2 = new Intent("2");
		Intent intent3 = new Intent("3");
		Intent intent4 = new Intent("1");
		Intent intent5 = new Intent("2");
		Intent intent6 = new Intent("4");

		IntentOperationList operations = new IntentOperationList();
		operations.add(Operator.ADD, intent1);
		operations.add(Operator.ADD, intent2);
		operations.add(Operator.ADD, intent3);
		assertEquals(3, operations.size());

		intents.executeOperations(operations);
		assertEquals(3, intents.getAllIntents().size());
		assertSame(intent1, intents.getIntent("1"));
		assertSame(intent2, intents.getIntent("2"));
		assertSame(intent3, intents.getIntent("3"));

		operations.clear();
		operations.add(Operator.ADD, intent4);
		operations.add(Operator.ADD, intent5);
		operations.add(Operator.ADD, intent6);
		assertEquals(3, operations.size());

		intents.executeOperations(operations);
		assertEquals(4, intents.getAllIntents().size());
		assertSame(intent4, intents.getIntent("1"));
		assertSame(intent5, intents.getIntent("2"));
		assertSame(intent3, intents.getIntent("3"));
		assertSame(intent6, intents.getIntent("4"));
	}

	@Test
	public void testRemoveOperation() {
		IntentMap intents = new IntentMap();

		Intent intent1 = new Intent("1");
		ShortestPathIntent intent2 =
				new ShortestPathIntent("2", 21L, 22L, 23L, 24L, 25L, 26L);
		ConstrainedShortestPathIntent intent3 =
				new ConstrainedShortestPathIntent("3", 31L, 32L, 33L, 34L, 35L, 36L, 1000.0);

		IntentOperationList operations = new IntentOperationList();
		operations.add(Operator.ADD, intent1);
		operations.add(Operator.ADD, intent2);
		operations.add(Operator.ADD, intent3);
		intents.executeOperations(operations);
		assertEquals(3, intents.getAllIntents().size());
		assertSame(intent1, intents.getIntent("1"));
		assertSame(intent2, intents.getIntent("2"));
		assertSame(intent3, intents.getIntent("3"));

		operations.clear();
		operations.add(Operator.REMOVE, new Intent("1"));
		operations.add(Operator.REMOVE, new Intent("3"));
		intents.executeOperations(operations);
		assertEquals(3, intents.getAllIntents().size());
		assertSame(intent1, intents.getIntent("1"));
		assertSame(intent2, intents.getIntent("2"));
		assertSame(intent3, intents.getIntent("3"));
		assertEquals(IntentState.DEL_REQ, intents.getIntent("1").getState());
		assertEquals(IntentState.CREATED, intents.getIntent("2").getState());
		assertEquals(IntentState.DEL_REQ, intents.getIntent("3").getState());
	}

	@Test
	public void testErrorOperation() {
		IntentMap intents = new IntentMap();
		IntentOperationList operations = new IntentOperationList();
		operations.add(Operator.ADD, new Intent("1", IntentState.CREATED));
		operations.add(Operator.ADD, new Intent("2", IntentState.INST_REQ));
		operations.add(Operator.ADD, new Intent("3", IntentState.INST_ACK));
		operations.add(Operator.ADD, new Intent("4", IntentState.INST_NACK));
		operations.add(Operator.ADD, new Intent("5", IntentState.REROUTE_REQ));
		operations.add(Operator.ADD, new Intent("6", IntentState.DEL_REQ));
		operations.add(Operator.ADD, new Intent("7", IntentState.DEL_ACK));
		operations.add(Operator.ADD, new Intent("8", IntentState.DEL_PENDING));
		intents.executeOperations(operations);
		assertEquals(8, intents.getAllIntents().size());

		operations.clear();
		operations.add(Operator.ERROR, new ErrorIntent(ErrorType.PATH_NOT_FOUND, "", new Intent("1")));
		operations.add(Operator.ERROR, new ErrorIntent(ErrorType.PATH_NOT_FOUND, "", new Intent("2")));
		operations.add(Operator.ERROR, new ErrorIntent(ErrorType.PATH_NOT_FOUND, "", new Intent("3")));
		operations.add(Operator.ERROR, new ErrorIntent(ErrorType.PATH_NOT_FOUND, "", new Intent("4")));
		operations.add(Operator.ERROR, new ErrorIntent(ErrorType.PATH_NOT_FOUND, "", new Intent("5")));
		operations.add(Operator.ERROR, new ErrorIntent(ErrorType.PATH_NOT_FOUND, "", new Intent("6")));
		operations.add(Operator.ERROR, new ErrorIntent(ErrorType.PATH_NOT_FOUND, "", new Intent("7")));
		operations.add(Operator.ERROR, new ErrorIntent(ErrorType.PATH_NOT_FOUND, "", new Intent("8")));
		intents.executeOperations(operations);

		assertEquals(IntentState.INST_NACK, intents.getIntent("1").getState());
		assertEquals(IntentState.INST_NACK, intents.getIntent("2").getState());
		assertEquals(IntentState.INST_NACK, intents.getIntent("3").getState());
		assertEquals(IntentState.INST_NACK, intents.getIntent("4").getState());
		assertEquals(IntentState.INST_NACK, intents.getIntent("5").getState());
		assertEquals(IntentState.DEL_PENDING, intents.getIntent("6").getState());
		assertEquals(IntentState.DEL_ACK, intents.getIntent("7").getState());
		assertEquals(IntentState.DEL_PENDING, intents.getIntent("8").getState());
	}

	@Test
	public void testPurge() {
		IntentMap intents = new IntentMap();
		IntentOperationList operations = new IntentOperationList();
		operations.add(Operator.ADD, new Intent("1", IntentState.CREATED));
		operations.add(Operator.ADD, new Intent("2", IntentState.INST_REQ));
		operations.add(Operator.ADD, new Intent("3", IntentState.INST_ACK));
		operations.add(Operator.ADD, new Intent("4", IntentState.INST_NACK));
		operations.add(Operator.ADD, new Intent("5", IntentState.REROUTE_REQ));
		operations.add(Operator.ADD, new Intent("6", IntentState.DEL_REQ));
		operations.add(Operator.ADD, new Intent("7", IntentState.DEL_ACK));
		operations.add(Operator.ADD, new Intent("8", IntentState.DEL_PENDING));
		intents.executeOperations(operations);
		assertEquals(8, intents.getAllIntents().size());

		intents.purge();

		assertEquals(6, intents.getAllIntents().size());
		assertEquals("1", intents.getIntent("1").getId());
		assertEquals("2", intents.getIntent("2").getId());
		assertEquals("3", intents.getIntent("3").getId());
		assertNull(intents.getIntent("4"));
		assertEquals("5", intents.getIntent("5").getId());
		assertEquals("6", intents.getIntent("6").getId());
		assertNull("7", intents.getIntent("7"));
		assertEquals("8", intents.getIntent("8").getId());
	}

	@Test
	public void testChangeStates() {
		IntentMap intents = new IntentMap();
		IntentOperationList operations = new IntentOperationList();
		operations.add(Operator.ADD, new Intent("1", IntentState.CREATED));
		operations.add(Operator.ADD, new Intent("2", IntentState.INST_REQ));
		operations.add(Operator.ADD, new Intent("3", IntentState.INST_ACK));
		operations.add(Operator.ADD, new Intent("4", IntentState.INST_NACK));
		operations.add(Operator.ADD, new Intent("5", IntentState.REROUTE_REQ));
		operations.add(Operator.ADD, new Intent("6", IntentState.DEL_REQ));
		operations.add(Operator.ADD, new Intent("7", IntentState.DEL_ACK));
		operations.add(Operator.ADD, new Intent("8", IntentState.DEL_PENDING));
		intents.executeOperations(operations);
		assertEquals(8, intents.getAllIntents().size());

		IntentStateList states = new IntentStateList();
		states.put("8", IntentState.CREATED);
		states.put("1", IntentState.INST_REQ);
		states.put("2", IntentState.INST_ACK);
		states.put("3", IntentState.INST_NACK);
		states.put("4", IntentState.REROUTE_REQ);
		states.put("5", IntentState.DEL_REQ);
		states.put("6", IntentState.DEL_ACK);
		states.put("7", IntentState.DEL_PENDING);
		intents.changeStates(states);

		assertEquals(IntentState.INST_REQ, intents.getIntent("1").getState());
		assertEquals(IntentState.INST_ACK, intents.getIntent("2").getState());
		assertEquals(IntentState.INST_NACK, intents.getIntent("3").getState());
		assertEquals(IntentState.REROUTE_REQ, intents.getIntent("4").getState());
		assertEquals(IntentState.DEL_REQ, intents.getIntent("5").getState());
		assertEquals(IntentState.DEL_ACK, intents.getIntent("6").getState());
		assertEquals(IntentState.DEL_PENDING, intents.getIntent("7").getState());
		assertEquals(IntentState.CREATED, intents.getIntent("8").getState());
	}
}
