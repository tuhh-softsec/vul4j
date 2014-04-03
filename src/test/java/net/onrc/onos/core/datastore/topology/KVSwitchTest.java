package net.onrc.onos.core.datastore.topology;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import net.onrc.onos.core.datastore.DataStoreClient;
import net.onrc.onos.core.datastore.IKVTable;
import net.onrc.onos.core.datastore.ObjectDoesntExistException;
import net.onrc.onos.core.datastore.ObjectExistsException;
import net.onrc.onos.core.datastore.WrongVersionException;
import net.onrc.onos.core.datastore.topology.KVSwitch;
import net.onrc.onos.core.datastore.topology.KVSwitch.STATUS;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class KVSwitchTest {
    IKVTable switchTable;
    static final Long dpid1 = 0x1L;
    KVSwitch sw1;

    @Before
    public void setUp() throws Exception {
	switchTable = DataStoreClient.getClient().getTable(KVSwitch.GLOBAL_SWITCH_TABLE_NAME);
	sw1 = new KVSwitch(dpid1);
    }

    @After
    public void tearDown() throws Exception {
	DataStoreClient.getClient().dropTable(switchTable);
    }

    public KVSwitch assertSwitchInDataStore(final Long dpid, final STATUS status) {
	try {
	    final KVSwitch sw = new KVSwitch(dpid);
	    sw.read();
	    assertNotEquals(DataStoreClient.getClient().VERSION_NONEXISTENT(), sw.getVersion());
	    assertEquals(dpid, sw.getDpid());
	    assertEquals(status, sw.getStatus());
	    return sw;
	} catch (ObjectDoesntExistException e) {
	    fail("Switch was not written to datastore");
	}
	return null;
    }
    public void assertSwitchNotInDataStore(final Long dpid) {
	final KVSwitch sw = new KVSwitch(dpid);
	try {
	    sw.read();
	    fail("Switch was not supposed to be there in datastore");
	} catch (ObjectDoesntExistException e) {
	}
    }

    @Test
    public void testGetAllSwitches() throws ObjectExistsException {
	final int NUM_SWITCHES = 100;
	Map<Long,KVSwitch> expected = new HashMap<>();
	for (long dpid = 1 ; dpid <= NUM_SWITCHES ; ++dpid) {
	    KVSwitch sw = new KVSwitch(dpid);
	    sw.setStatus(STATUS.ACTIVE);
	    sw.create();
	    assertNotEquals(DataStoreClient.getClient().VERSION_NONEXISTENT(), sw.getVersion());
	    expected.put(sw.getDpid(), sw);
	}

	Iterable<KVSwitch> switches = KVSwitch.getAllSwitches();

	for (KVSwitch sw : switches) {
	    KVSwitch expectedSw = expected.get(sw.getDpid());
	    assertNotNull(expectedSw);
	    assertEquals(expectedSw.getDpid(), sw.getDpid());
	    assertEquals(expectedSw.getStatus(), sw.getStatus());
	    assertEquals(expectedSw.getVersion(), sw.getVersion());

	    assertArrayEquals(expectedSw.getKey(), sw.getKey());
	}
    }

    @Test
    public void testCreate() throws ObjectExistsException {
	sw1.setStatus(STATUS.ACTIVE);
	sw1.create();
	assertNotEquals(DataStoreClient.getClient().VERSION_NONEXISTENT(), sw1.getVersion());

	assertEquals(dpid1, sw1.getDpid());
	assertEquals(STATUS.ACTIVE, sw1.getStatus());

	assertSwitchInDataStore(dpid1, STATUS.ACTIVE);
    }

    @Test(expected = ObjectExistsException.class)
    public void testCreateFailAlreadyExist() throws ObjectExistsException {
	// setup pre-existing Switch
	KVSwitch sw = new KVSwitch(dpid1);
	sw.forceCreate();
	assertNotEquals(DataStoreClient.getClient().VERSION_NONEXISTENT(), sw.getVersion());
	assertSwitchInDataStore(dpid1, STATUS.INACTIVE);

	sw1.setStatus(STATUS.ACTIVE);
	sw1.create();
	fail("Should have thrown an exception");
    }

    @Test
    public void testForceCreate() {
	// setup pre-existing Switch
	KVSwitch sw = new KVSwitch(dpid1);
	sw.forceCreate();
	assertNotEquals(DataStoreClient.getClient().VERSION_NONEXISTENT(), sw.getVersion());
	assertSwitchInDataStore(dpid1, STATUS.INACTIVE);


	sw1.setStatus(STATUS.ACTIVE);
	sw1.forceCreate();
	assertNotEquals(DataStoreClient.getClient().VERSION_NONEXISTENT(), sw1.getVersion());

	assertEquals(dpid1, sw1.getDpid());
	assertEquals(STATUS.ACTIVE, sw1.getStatus());
	assertSwitchInDataStore(dpid1, STATUS.ACTIVE);
    }

    @Test
    public void testRead() throws ObjectDoesntExistException {
	// setup pre-existing Switch
	KVSwitch sw = new KVSwitch(dpid1);
	sw.setStatus(STATUS.ACTIVE);
	sw.forceCreate();
	assertNotEquals(DataStoreClient.getClient().VERSION_NONEXISTENT(), sw.getVersion());
	assertSwitchInDataStore(dpid1, STATUS.ACTIVE);

	sw1.read();
	assertNotEquals(DataStoreClient.getClient().VERSION_NONEXISTENT(), sw1.getVersion());
	assertEquals(sw.getVersion(), sw1.getVersion());
	assertEquals(dpid1, sw1.getDpid());
	assertEquals(STATUS.ACTIVE, sw1.getStatus());
    }

    @Test(expected = ObjectDoesntExistException.class)
    public void testReadFailNoExist() throws ObjectDoesntExistException {

	sw1.read();
	fail("Should have thrown an exception");
    }

    @Test
    public void testUpdate() throws ObjectDoesntExistException, WrongVersionException {
	// setup pre-existing Switch
	KVSwitch sw = new KVSwitch(dpid1);
	sw.setStatus(STATUS.ACTIVE);
	sw.forceCreate();
	assertNotEquals(DataStoreClient.getClient().VERSION_NONEXISTENT(), sw.getVersion());
	assertSwitchInDataStore(dpid1, STATUS.ACTIVE);


	sw1.read();
	assertNotEquals(DataStoreClient.getClient().VERSION_NONEXISTENT(), sw1.getVersion());

	sw1.setStatus(STATUS.INACTIVE);
	sw1.update();
	assertNotEquals(DataStoreClient.getClient().VERSION_NONEXISTENT(), sw1.getVersion());
	assertNotEquals(sw.getVersion(), sw1.getVersion());
	assertEquals(dpid1, sw1.getDpid());
	assertEquals(STATUS.INACTIVE, sw1.getStatus());
    }

    @Test
    public void testDelete() throws ObjectDoesntExistException, WrongVersionException {
	// setup pre-existing Switch
	KVSwitch sw = new KVSwitch(dpid1);
	sw.setStatus(STATUS.ACTIVE);
	sw.forceCreate();
	assertNotEquals(DataStoreClient.getClient().VERSION_NONEXISTENT(), sw.getVersion());
	assertSwitchInDataStore(dpid1, STATUS.ACTIVE);


	try {
	    sw1.read();
	} catch (ObjectDoesntExistException e) {
	    fail("Failed reading switch to delete");
	}
	assertNotEquals(DataStoreClient.getClient().VERSION_NONEXISTENT(), sw1.getVersion());
	sw1.delete();
	assertSwitchNotInDataStore(dpid1);
    }

    @Test
    public void testForceDelete() {
	// setup pre-existing Switch
	KVSwitch sw = new KVSwitch(dpid1);
	sw.setStatus(STATUS.ACTIVE);
	sw.forceCreate();
	assertNotEquals(DataStoreClient.getClient().VERSION_NONEXISTENT(), sw.getVersion());
	assertSwitchInDataStore(dpid1, STATUS.ACTIVE);


	sw1.forceDelete();
	assertNotEquals(DataStoreClient.getClient().VERSION_NONEXISTENT(), sw1.getVersion());
    }

}
