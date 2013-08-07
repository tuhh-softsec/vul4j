package net.onrc.onos.ofcontroller.bgproute;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PatriciaTrieTest {

	IPatriciaTrie ptrie;
	Prefix[] prefixes;
	Map<Prefix, Rib> mappings;
	
	@Before
	public void setUp() throws Exception {
		ptrie = new PatriciaTrie(32);
		mappings = new HashMap<Prefix, Rib>();
		
		prefixes = new Prefix[] {
			new Prefix("192.168.10.0", 24),
			new Prefix("192.168.8.0", 23),
			new Prefix("192.168.8.0", 22),
			new Prefix("192.0.0.0", 7),
			new Prefix("192.168.11.0", 24),
			new Prefix("10.0.23.128", 25),
			new Prefix("206.17.144.0", 20),
			new Prefix("9.17.0.0", 12),
			new Prefix("192.168.0.0", 16)
		};
				
		for (int i = 0; i < prefixes.length; i++) {
			mappings.put(prefixes[i], new Rib("192.168.10.101", "192.168.20." + i, 32));
			ptrie.put(prefixes[i], new Rib("192.168.10.101", "192.168.20." + i, 32));
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPut() {
		IPatriciaTrie ptrie = new PatriciaTrie(32);
		
		Prefix p1 = new Prefix("192.168.240.0", 20);
		Rib r1 = new Rib("192.168.10.101", "192.168.60.2", 20);
		Rib retval = ptrie.put(p1, r1);
		assertNull(retval);
		retval = ptrie.lookup(p1);
		assertTrue(r1 == retval); //should be the same object
		
		Prefix p2 = new Prefix("192.160.0.0", 12);
		Rib r2 = new Rib("192.168.10.101", "192.168.20.1", 12);
		retval = ptrie.put(p2, r2);
		assertNull(retval);
		
		Prefix p3 = new Prefix("192.168.208.0", 20);
		Rib r3 = new Rib("192.168.10.101", "192.168.30.1", 20);
		retval = ptrie.put(p3,  r3);
		assertNull(retval);
		
		//Insert a new Rib entry over a previous one
		Rib r3new = new Rib("192.168.10.101", "192.168.60.2", 20);
		retval = ptrie.put(p3, r3new);
		assertNotNull(retval);
		assertTrue(retval.equals(r3));
		assertTrue(retval == r3); //should be the same object
		
		//Now we have an aggregate node with prefix 192.168.192.0/18.
		//We will insert a Rib at this prefix
		Prefix p4 = new Prefix("192.168.192.0", 18);
		Rib r4 = new Rib("192.168.10.101", "192.168.40.1", 18);
		retval = ptrie.put(p4, r4);
		assertNull(retval);
		retval = ptrie.lookup(p4);
		assertTrue(retval == r4); //should be the same object
	}

	@Test
	public void testLookup() {
		for (Map.Entry<Prefix, Rib> entry : mappings.entrySet()) {
			Rib r = ptrie.lookup(entry.getKey());
			assertTrue(entry.getValue().equals(r));
		}
		
		//These are aggregate nodes in the tree. Shouldn't be returned by lookup
		Prefix p1 = new Prefix("0.0.0.0", 0);
		Rib retval = ptrie.lookup(p1);
		assertNull(retval);
		
		//We'll put a Rib at an aggregate node and check if lookup returns correctly
		Prefix p2 = new Prefix("192.0.0.0", 4);
		Rib r2 = new Rib("192.168.10.101", "192.168.60.1", 4);
		retval = ptrie.put(p2, r2);
		assertNull(retval);
		retval = ptrie.lookup(p2);
		assertTrue(retval.equals(r2));
	}

	@Ignore
	@Test
	public void testMatch() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemove() {
		Prefix p1 = new Prefix("192.168.8.0", 23);
		Rib retval = ptrie.lookup(p1);
		assertNotNull(retval);
		boolean success = ptrie.remove(p1, retval);
		assertTrue(success);
		
		Prefix p2 = new Prefix("192.168.8.0", 22);
		Prefix p3 = new Prefix("192.168.10.0", 24);
		
		//Test it does the right thing with null arguments
		success = ptrie.remove(null, null);
		assertFalse(success);
		success = ptrie.remove(p2, null);
		assertFalse(success);
		
		//Check other prefixes are still there
		retval = ptrie.lookup(p2);
		assertNotNull(retval);
		retval = ptrie.lookup(p3);
		assertNotNull(retval);
		
		Prefix p4 = new Prefix("9.17.0.0", 12);
		retval = ptrie.lookup(p4);
		assertNotNull(retval);
		success = ptrie.remove(p4, retval);
		assertTrue(success);
		success = ptrie.remove(p4, retval);
		assertFalse(success);
		
		//Check other prefixes are still there
		retval = ptrie.lookup(p2);
		assertNotNull(retval);
		retval = ptrie.lookup(p3);
		assertNotNull(retval);
		
		Prefix p5 = new Prefix("192.0.0.0", 7);
		retval = ptrie.lookup(p5);
		assertNotNull(retval);
		success = ptrie.remove(p5, retval);
		assertTrue(success);
		
		//Check other prefixes are still there
		retval = ptrie.lookup(p2);
		assertNotNull(retval);
		retval = ptrie.lookup(p3);
		assertNotNull(retval);
		
		
	}

	@Test(expected=java.util.NoSuchElementException.class)
	public void testIterator() {		
		int[] order = new int[] {7, 5, 3, 8, 2, 1, 0, 4, 6};
		
		Iterator<IPatriciaTrie.Entry> it = ptrie.iterator();
		int i = 0;
		assertTrue(it.hasNext());
		while (it.hasNext()) {
			IPatriciaTrie.Entry entry = it.next();
			assertTrue(entry.getPrefix().equals(prefixes[order[i]]));
			i++;
		}
		assertFalse(it.hasNext());
		assertTrue(i == order.length);
		
		IPatriciaTrie pt = new PatriciaTrie(32);
		Iterator<IPatriciaTrie.Entry> it2 = pt.iterator();
		assertFalse(it2.hasNext());
		it.next(); //throws NoSuchElementException
	}

}
