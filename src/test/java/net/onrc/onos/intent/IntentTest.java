package net.onrc.onos.intent;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class IntentTest {
	@Test
	public void testCreateIntent() {
		Intent intent = new Intent("id");
		assertEquals("id", intent.getId());
		assertEquals(Intent.IntentState.CREATED, intent.getState());
	}

	@Test
	public void testCreateIntentWithState() {
		Intent intent = new Intent("id", Intent.IntentState.INST_REQ);
		assertEquals("id", intent.getId());
		assertEquals(Intent.IntentState.INST_REQ, intent.getState());
	}

	@Test
	public void testSetState() {
		Intent intent = new Intent("id");

		intent.setState(Intent.IntentState.INST_REQ);
		assertEquals(Intent.IntentState.INST_REQ, intent.getState());

		intent.setState(Intent.IntentState.DEL_REQ);
		assertEquals(Intent.IntentState.DEL_REQ, intent.getState());
	}

	@Test
	public void testEquals() {
		Intent intent1 = new Intent("id1");
		Intent intent2 = new Intent("id1");
		Intent intent3 = new Intent("id2");
		Intent intent4 = new Intent("id2");

		assertEquals(intent1, intent2);
		assertEquals(intent3, intent4);

		assertFalse(intent1.equals(intent3));
		assertFalse(intent3.equals(intent1));

		intent1.setState(Intent.IntentState.INST_ACK);
		intent2.setState(Intent.IntentState.INST_NACK);
		assertEquals(intent1, intent2);
	}

	@Test
	public void testHashCode() {
		Intent intent1 = new Intent("id1");
		intent1.setState(Intent.IntentState.INST_ACK);
		Intent intent2 = new Intent("id1");
		intent2.setState(Intent.IntentState.INST_NACK);
		Intent intent3 = new Intent("id2");
		Intent intent4 = new Intent("id2");

		HashSet<Intent> intents = new HashSet<>();
		intents.add(intent1);
		intents.add(intent2);
		intents.add(intent3);
		intents.add(intent4);

		assertEquals(2, intents.size());
		assertTrue(intents.contains(intent1));
		assertTrue(intents.contains(intent2));
		assertTrue(intents.contains(intent3));
		assertTrue(intents.contains(intent4));
	}
}
