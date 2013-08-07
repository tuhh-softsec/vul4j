package net.onrc.onos.ofcontroller.bgproute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.InetAddresses;

public class PtreeTest {
	
	private Logger log = LoggerFactory.getLogger(PtreeTest.class);
	
	private Ptree ptree;
	private PatriciaTrie ooptrie;
	
	private Map<String, byte[]> byteAddresses;

	@Before
	public void setUp() throws Exception {
		ptree = new Ptree(32);
		ooptrie = new PatriciaTrie(32);
			
		String[] strPrefixes = {
			"192.168.10.0/24",
			"192.168.10.0/23",
			"192.168.10.0/22",
			"192.0.0.0/7",
			"192.168.11.0/24",
			"10.0.23.128/25",
			"206.17.144.0/20",
			"9.17.0.0/12",
			"192.168.0.0/16"
		};
		
		byteAddresses = new HashMap<String, byte[]>(strPrefixes.length+10);
		for (String prefix : strPrefixes) {
			String address = prefix.split("/")[0];
			int prefixLength = Integer.parseInt(prefix.split("/")[1]);
			byteAddresses.put(prefix, InetAddresses.forString(address).getAddress());
			
			PtreeNode node = ptree.acquire(byteAddresses.get(prefix), prefixLength);
			node.rib = new Rib("192.168.10.101", "192.168.60.1", prefixLength);
			ooptrie.put(new Prefix(byteAddresses.get(prefix), prefixLength), 
					new Rib("192.168.10.101", "192.168.60.1", prefixLength));
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Ignore
	@Test
	public void testAcquireByteArray() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testAcquireByteArrayInt() {
		//First let's test an empty Ptree
		Ptree localPtree = new Ptree(32);
		PtreeNode node = localPtree.acquire(new byte[] {0x00, 0x00, 0x00, 0x00});
		assertTrue(node != null && node.rib == null);
		
		//Now let's look at the prepopulated tree
		String testPrefix = "206.17.144.0/20";
		PtreeNode existingNode = ptree.acquire(byteAddresses.get(testPrefix), 20);
		printByteArray(existingNode.key);
		printByteArray(byteAddresses.get(testPrefix));
		assertTrue(existingNode != null && existingNode.rib == null);
		
		assertTrue(Arrays.equals(existingNode.key, byteAddresses.get(testPrefix)));
	}

	@Test
	public void testLookup() {
		String prefix1 = "192.168.10.12";
		int length1 = 29;
		PtreeNode node1 = ptree.lookup(InetAddresses.forString(prefix1).getAddress(), length1);
		
		//There should be no direct match
		assertTrue(node1 == null);
		
		log.debug("{} null: {}", "node1", node1 == null ? "true" : "false");
		
		String prefix2 = "206.17.144.0";
		int length2 = 20;
		PtreeNode node2 = ptree.lookup(InetAddresses.forString(prefix2).getAddress(), length2);
		
		assertTrue(node2 != null);
		assertTrue(Arrays.equals(node2.key, byteAddresses.get(prefix2 + "/" + length2)));
		
		log.debug("{} null: {}", "node2", node2 == null ? "true" : "false");
		if (node2 != null) {
			log.debug("{} key: {}, keybits: {}", new Object[] {"node2", node2.key, node2.keyBits});
		}
		
		String prefix3 = "192.0.0.0";
		int length3 = 7;
		PtreeNode node3 = ptree.lookup(InetAddresses.forString(prefix3).getAddress(), length3);
		assertTrue(node3 != null);
	}

	@Test
	public void testMatch() {
		String prefix1 = "192.168.10.12";
		int length1 = 29;
		PtreeNode node1 = ptree.match(InetAddresses.forString(prefix1).getAddress(), length1);
		
		//There should be no direct match, but we should get the covering prefix
		assertTrue(node1 != null);
		assertTrue(Arrays.equals(node1.key, byteAddresses.get("192.168.10.0/24")));
		
		log.debug("{} null: {}", "node1", node1 == null ? "true" : "false");
		if (node1 != null) {
			log.debug("{} key: {}, keybits: {}", new Object[] {"node1", node1.key, node1.keyBits});
		}
	}
	
	@Ignore
	@Test
	public void testTraverse() {
		
		String expected = "[0, 0, 0, 0]/0\n";
		expected += "[8, 0, 0, 0]/6\n";
		expected += "[9, 17, 0, 0]/12\n";
		expected += "[10, 0, 23, -128]/25\n";
		expected += "[-64, 0, 0, 0]/4\n";
		expected += "[-64, -88, 0, 0]/16\n";
		expected += "[-64, -88, 8, 0]/22\n";
		expected += "[-64, -88, 10, 0]/23\n";
		expected += "[-64, -88, 10, 0]/24\n";
		expected += "[-64, -88, 11, 0]/24\n";
		expected += "[-50, 17, -112, 0]/20\n";
		
		PtreeNode node;
		String result = "";
		
		for (node = ptree.begin(); node != null; node = ptree.next(node)) {
			result += printByteArray(node.key) + "/" + node.keyBits + "\n";
		}
		
		assertEquals(expected, result);
	}

	@Ignore
	@Test
	public void testBegin() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testNext() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testDelReference() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testMisc() {
		int bitIndex = -1;
		int index = (int)(bitIndex / Byte.SIZE);
	    int bit = (int)(bitIndex % Byte.SIZE);
	    
	    log.debug("index {} bit {}", index, bit); 
	    log.debug("percent {}", 1%8);
	    
	    //PtreeNode node1 = new PtreeNode(new byte[] {0x0, 0x0, 0x0, 0x0}, 0, 4);
	    PtreeNode node1 = new PtreeNode(null, 0, 4);
	    log.debug("node1: key {}, keybits {}", printByteArray(node1.key), node1.keyBits);
	    
	    //PtreeNode node2 = new PtreeNode(new byte[] {(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff}, 
	    PtreeNode node2 = new PtreeNode(null,
	    		32, 4);
	    log.debug("node2: key {}, keybits {}", printByteArray(node2.key), node2.keyBits);
	}
	
	@Test
	public void testIteration() {
		Iterator<IPatriciaTrie.Entry> it = ooptrie.iterator();
		
		while (it.hasNext()) {
			IPatriciaTrie.Entry entry = it.next();
			log.debug("PatriciaTrie prefix {} \t {}", entry.getPrefix(), entry.getPrefix().printAsBits());
		}
		
		try {
			PtreeNode node;
			for (node = ptree.begin(); node != null; node = ptree.next(node)) {
				log.debug("Ptree prefix {}/{}", InetAddress.getByAddress(node.key).getHostAddress(), node.keyBits);
			}
		} catch (UnknownHostException e) {
			
		}
	}
	
	private String printByteArray(byte[] array) {
		String result = "[";
		for (byte b : array) {
			result += b + ", ";
		}
		result = result.substring(0, result.length() - 2);
		result += "]";
		return result;
	}

}
