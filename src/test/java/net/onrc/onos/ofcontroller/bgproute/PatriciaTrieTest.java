package net.onrc.onos.ofcontroller.bgproute;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PatriciaTrieTest {

	IPatriciaTrie<RibEntry> ptrie;
	Prefix[] prefixes;
	Map<Prefix, RibEntry> mappings;
	
	@Before
	public void setUp() throws Exception {
		ptrie = new PatriciaTrie<RibEntry>(32);
		mappings = new HashMap<Prefix, RibEntry>();
		
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
			mappings.put(prefixes[i], new RibEntry("192.168.10.101", "192.168.20." + i));
			ptrie.put(prefixes[i], new RibEntry("192.168.10.101", "192.168.20." + i));
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPut() {
		IPatriciaTrie<RibEntry> ptrie = new PatriciaTrie<RibEntry>(32);
		
		Prefix p1 = new Prefix("192.168.240.0", 20);
		RibEntry r1 = new RibEntry("192.168.10.101", "192.168.60.2");
		RibEntry retval = ptrie.put(p1, r1);
		assertNull(retval);
		retval = ptrie.lookup(p1);
		assertTrue(r1 == retval); //should be the same object
		
		Prefix p2 = new Prefix("192.160.0.0", 12);
		RibEntry r2 = new RibEntry("192.168.10.101", "192.168.20.1");
		retval = ptrie.put(p2, r2);
		assertNull(retval);
		
		Prefix p3 = new Prefix("192.168.208.0", 20);
		RibEntry r3 = new RibEntry("192.168.10.101", "192.168.30.1");
		retval = ptrie.put(p3,  r3);
		assertNull(retval);
		
		//Insert a new RibEntry entry over a previous one
		RibEntry r3new = new RibEntry("192.168.10.101", "192.168.60.2");
		retval = ptrie.put(p3, r3new);
		assertNotNull(retval);
		assertTrue(retval.equals(r3));
		assertTrue(retval == r3); //should be the same object
		
		//Now we have an aggregate node with prefix 192.168.192.0/18.
		//We will insert a RibEntry at this prefix
		Prefix p4 = new Prefix("192.168.192.0", 18);
		RibEntry r4 = new RibEntry("192.168.10.101", "192.168.40.1");
		retval = ptrie.put(p4, r4);
		assertNull(retval);
		retval = ptrie.lookup(p4);
		assertTrue(retval == r4); //should be the same object
	}

	@Test
	public void testLookup() {
		for (Map.Entry<Prefix, RibEntry> entry : mappings.entrySet()) {
			RibEntry r = ptrie.lookup(entry.getKey());
			assertTrue(entry.getValue().equals(r));
		}
		
		//These are aggregate nodes in the tree. Shouldn't be returned by lookup
		Prefix p1 = new Prefix("0.0.0.0", 0);
		RibEntry retval = ptrie.lookup(p1);
		assertNull(retval);
		
		//We'll put a RibEntry at an aggregate node and check if lookup returns correctly
		Prefix p2 = new Prefix("192.0.0.0", 4);
		RibEntry r2 = new RibEntry("192.168.10.101", "192.168.60.1");
		retval = ptrie.put(p2, r2);
		assertNull(retval);
		retval = ptrie.lookup(p2);
		assertTrue(retval.equals(r2));
	}

	//@Ignore
	@Test
	public void testMatch() {
		Prefix p1 = new Prefix("192.168.10.30", 32);
		Prefix p2 = new Prefix("192.168.10.30", 31);
		Prefix p3 = new Prefix("192.168.8.241", 32);
		Prefix p4 = new Prefix("1.0.0.0", 32);
		Prefix p5 = new Prefix("192.168.8.0", 22);
		Prefix p6 = new Prefix("192.168.8.0", 21);
		
		assertTrue(ptrie.match(p1).equals(mappings.get(prefixes[0])));
		assertTrue(ptrie.match(p2).equals(mappings.get(prefixes[0])));
		assertTrue(ptrie.match(p3).equals(mappings.get(prefixes[1])));
		assertNull(ptrie.match(p4));
		assertTrue(ptrie.match(p5).equals(mappings.get(prefixes[2])));
		//System.out.println(ptrie.match(p6).getNextHop().getHostAddress());
		assertTrue(ptrie.match(p6).equals(mappings.get(prefixes[8])));
		
		
		//TODO more extensive tests
		//fail("Not yet implemented");
	}

	@Test
	public void testRemove() {
		Prefix p1 = new Prefix("192.168.8.0", 23);
		RibEntry retval = ptrie.lookup(p1);
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
		
		Iterator<IPatriciaTrie.Entry<RibEntry>> it = ptrie.iterator();
		int i = 0;
		assertTrue(it.hasNext());
		while (it.hasNext()) {
			IPatriciaTrie.Entry<RibEntry> entry = it.next();
			assertTrue(entry.getPrefix().equals(prefixes[order[i]]));
			i++;
		}
		assertFalse(it.hasNext());
		assertTrue(i == order.length);
		
		IPatriciaTrie<RibEntry> pt = new PatriciaTrie<RibEntry>(32);
		Iterator<IPatriciaTrie.Entry<RibEntry>> it2 = pt.iterator();
		assertFalse(it2.hasNext());
		it.next(); //throws NoSuchElementException
	}

}
