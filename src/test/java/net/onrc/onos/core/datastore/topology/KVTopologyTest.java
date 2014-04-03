package net.onrc.onos.core.datastore.topology;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.onrc.onos.core.datastore.DataStoreClient;
import net.onrc.onos.core.datastore.IKVClient;
import net.onrc.onos.core.datastore.IKVTable;
import net.onrc.onos.core.datastore.ObjectDoesntExistException;
import net.onrc.onos.core.datastore.ObjectExistsException;
import net.onrc.onos.core.datastore.WrongVersionException;
import net.onrc.onos.core.datastore.utils.ByteArrayComparator;
import net.onrc.onos.core.datastore.utils.KVObject;
import net.onrc.onos.core.datastore.utils.KVObject.WriteOp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class KVTopologyTest {

    static final long VERSION_NONEXISTENT = DataStoreClient.getClient().VERSION_NONEXISTENT();

    private static final byte[] DEVICE2_MAC_SW2P2 = new byte[]{6, 5, 4, 3, 2, 1, 0};

    private static final Long SW2_PORTNO2 = 2L;

    private static final Long SW2_PORTNO1 = 1L;

    private static final Long DPID2 = 0x2L;

    private static final byte[] DEVICE1_MAC_SW1P1 = new byte[]{0, 1, 2, 3, 4, 5, 6};

    private static final Long SW1_PORTNO2 = 2L;

    private static final Long SW1_PORTNO1 = 1L;

    private static final Long DPID1 = 0x1L;

    @Before
    @After
    public void wipeTopology() throws Exception {
        IKVTable switchTable = DataStoreClient.getClient().getTable(KVSwitch.GLOBAL_SWITCH_TABLE_NAME);
        DataStoreClient.getClient().dropTable(switchTable);

        IKVTable portTable = DataStoreClient.getClient().getTable(KVPort.GLOBAL_PORT_TABLE_NAME);
        DataStoreClient.getClient().dropTable(portTable);

        IKVTable linkTable = DataStoreClient.getClient().getTable(KVLink.GLOBAL_LINK_TABLE_NAME);
        DataStoreClient.getClient().dropTable(linkTable);

        IKVTable deviceTable = DataStoreClient.getClient().getTable(KVDevice.GLOBAL_DEVICE_TABLE_NAME);
        DataStoreClient.getClient().dropTable(deviceTable);
    }

    @Test
    public void basic_switch_test() {
        // create switch 0x1
        try {
            KVSwitch sw = new KVSwitch(DPID1);
            sw.setStatus(KVSwitch.STATUS.ACTIVE);
            sw.create();
            assertNotEquals(VERSION_NONEXISTENT, sw.getVersion());
            assertEquals(DPID1, sw.getDpid());
            assertEquals(KVSwitch.STATUS.ACTIVE, sw.getStatus());
        } catch (ObjectExistsException e) {
            e.printStackTrace();
            fail("Create Switch Failed " + e);
        }

        // read switch 0x1
        KVSwitch swRead = new KVSwitch(DPID1);
        try {
            swRead.read();
            assertNotEquals(VERSION_NONEXISTENT, swRead.getVersion());
            assertEquals(DPID1, swRead.getDpid());
            assertEquals(KVSwitch.STATUS.ACTIVE, swRead.getStatus());
        } catch (ObjectDoesntExistException e) {
            e.printStackTrace();
            fail("Reading Switch Failed " + e);
        }

        // and update 0x1
        swRead.setStatus(KVSwitch.STATUS.INACTIVE);
        try {
            swRead.update();
            assertNotEquals(VERSION_NONEXISTENT, swRead.getVersion());
            assertEquals(DPID1, swRead.getDpid());
            assertEquals(KVSwitch.STATUS.INACTIVE, swRead.getStatus());
        } catch (ObjectDoesntExistException | WrongVersionException e) {
            e.printStackTrace();
            fail("Updating Switch Failed " + e);
        }

        // read 0x1 again and delete
        KVSwitch swRead2 = new KVSwitch(DPID1);
        try {
            swRead2.read();
            assertNotEquals(VERSION_NONEXISTENT, swRead2.getVersion());
            assertEquals(DPID1, swRead2.getDpid());
            assertEquals(KVSwitch.STATUS.INACTIVE, swRead2.getStatus());
        } catch (ObjectDoesntExistException e) {
            e.printStackTrace();
            fail("Reading Switch Again Failed " + e);
        }

        try {
            swRead2.delete();
            assertNotEquals(VERSION_NONEXISTENT, swRead2.getVersion());
        } catch (ObjectDoesntExistException | WrongVersionException e) {
            e.printStackTrace();
            fail("Deleting Switch Failed " + e);
        }

        // make sure 0x1 is deleted
        KVObject swRead3 = new KVSwitch(DPID1);
        try {
            swRead3.read();
            fail(swRead3 + " was supposed to be deleted, but read succeed");
        } catch (ObjectDoesntExistException e) {
            System.out.println("-- " + swRead3 + " not found as expected--");
            e.printStackTrace(System.out);
            System.out.println("---------------------------------------");
        }
    }

    @Test
    public void topology_setup_and_tear_down() {
        topology_setup();
        topology_walk();
        topology_delete();
    }

    private static void topology_setup() {

        // d1 - s1p1 - s1 - s1p2 - s2p1 - s2 - s2p2

        KVSwitch sw1 = new KVSwitch(DPID1);
        sw1.setStatus(KVSwitch.STATUS.ACTIVE);
        try {
            sw1.create();
            assertNotEquals(VERSION_NONEXISTENT, sw1.getVersion());
            assertEquals(DPID1, sw1.getDpid());
            assertEquals(KVSwitch.STATUS.ACTIVE, sw1.getStatus());
        } catch (ObjectExistsException e) {
            e.printStackTrace();
            fail("Switch creation failed " + e);
        }

        KVPort sw1p1 = new KVPort(DPID1, SW1_PORTNO1);
        sw1p1.setStatus(KVPort.STATUS.ACTIVE);
        KVPort sw1p2 = new KVPort(DPID1, SW1_PORTNO2);
        sw1p2.setStatus(KVPort.STATUS.ACTIVE);
        try {
            sw1p1.create();
            assertNotEquals(VERSION_NONEXISTENT, sw1p1.getVersion());
            assertEquals(DPID1, sw1p1.getDpid());
            assertEquals(SW1_PORTNO1, sw1p1.getNumber());
            assertEquals(KVPort.STATUS.ACTIVE, sw1p1.getStatus());

            sw1p2.create();
            assertNotEquals(VERSION_NONEXISTENT, sw1p2.getVersion());
            assertEquals(DPID1, sw1p2.getDpid());
            assertEquals(SW1_PORTNO2, sw1p2.getNumber());
            assertEquals(KVPort.STATUS.ACTIVE, sw1p2.getStatus());
        } catch (ObjectExistsException e) {
            e.printStackTrace();
            fail("Port creation failed " + e);
        }

        try {
            sw1.update();
            assertNotEquals(VERSION_NONEXISTENT, sw1.getVersion());
            assertEquals(DPID1, sw1.getDpid());
            assertEquals(KVSwitch.STATUS.ACTIVE, sw1.getStatus());
        } catch (ObjectDoesntExistException | WrongVersionException e) {
            e.printStackTrace();
            fail("Switch update failed " + e);
        }

        KVDevice d1 = new KVDevice(DEVICE1_MAC_SW1P1);
        d1.addPortId(sw1p1.getId());

        try {
            d1.create();
            assertNotEquals(VERSION_NONEXISTENT, d1.getVersion());
            assertEquals(1, d1.getAllPortIds().size());
            assertArrayEquals(sw1p1.getId(), d1.getAllPortIds().iterator().next());

            try {
                sw1p1.update();
                assertNotEquals(VERSION_NONEXISTENT, sw1p1.getVersion());
                assertEquals(DPID1, sw1p1.getDpid());
                assertEquals(SW1_PORTNO1, sw1p1.getNumber());
                assertEquals(KVPort.STATUS.ACTIVE, sw1p1.getStatus());
            } catch (ObjectDoesntExistException | WrongVersionException e) {
                e.printStackTrace();
                fail("Link update failed " + e);
            }
        } catch (ObjectExistsException e) {
            e.printStackTrace();
            fail("Device creation failed " + e);
        }

        KVSwitch sw2 = new KVSwitch(DPID2);
        sw2.setStatus(KVSwitch.STATUS.ACTIVE);
        KVPort sw2p1 = new KVPort(DPID2, SW2_PORTNO1);
        sw2p1.setStatus(KVPort.STATUS.ACTIVE);
        KVPort sw2p2 = new KVPort(DPID2, SW2_PORTNO2);
        sw2p2.setStatus(KVPort.STATUS.ACTIVE);

        KVDevice d2 = new KVDevice(DEVICE2_MAC_SW2P2);
        d2.addPortId(sw2p2.getId());

        IKVClient client = DataStoreClient.getClient();

        List<WriteOp> groupOp = Arrays.asList(
                sw2.createOp(client), sw2p1.createOp(client),
                sw2p2.createOp(client), d2.createOp(client));
        boolean failed = KVObject.multiWrite(groupOp);
        if (failed) {
            for (WriteOp op : groupOp) {
                System.err.println(op);
            }
            fail("Some of Switch/Port/Device creation failed");
        } else {
            assertNotEquals(VERSION_NONEXISTENT, sw2.getVersion());
            assertEquals(DPID2, sw2.getDpid());
            assertEquals(KVSwitch.STATUS.ACTIVE, sw2.getStatus());

            assertNotEquals(VERSION_NONEXISTENT, sw2p1.getVersion());
            assertEquals(DPID2, sw2p1.getDpid());
            assertEquals(SW2_PORTNO1, sw2p1.getNumber());
            assertEquals(KVPort.STATUS.ACTIVE, sw2p1.getStatus());

            assertNotEquals(VERSION_NONEXISTENT, sw2p2.getVersion());
            assertEquals(DPID2, sw2p2.getDpid());
            assertEquals(SW2_PORTNO2, sw2p2.getNumber());
            assertEquals(KVPort.STATUS.ACTIVE, sw2p2.getStatus());

            assertNotEquals(VERSION_NONEXISTENT, d2.getVersion());
            assertEquals(1, d2.getAllPortIds().size());
            assertArrayEquals(sw2p2.getId(), d2.getAllPortIds().iterator().next());
        }

        KVLink l1 = new KVLink(DPID1, SW1_PORTNO2, DPID2, SW2_PORTNO1);
        l1.setStatus(KVLink.STATUS.ACTIVE);

        try {
            l1.create();
            assertNotEquals(VERSION_NONEXISTENT, l1.getVersion());
            assertEquals(KVLink.STATUS.ACTIVE, l1.getStatus());
            assertArrayEquals(sw1.getId(), l1.getSrc().getSwitchID());
            assertArrayEquals(sw1p2.getId(), l1.getSrc().getPortID());
            assertArrayEquals(sw2.getId(), l1.getDst().getSwitchID());
            assertArrayEquals(sw2p1.getId(), l1.getDst().getPortID());

            try {
                sw1p2.update();
                assertNotEquals(VERSION_NONEXISTENT, sw1p2.getVersion());
                assertEquals(DPID1, sw1p2.getDpid());
                assertEquals(SW1_PORTNO2, sw1p2.getNumber());
                assertEquals(KVPort.STATUS.ACTIVE, sw1p2.getStatus());

                sw2p1.update();
                assertNotEquals(VERSION_NONEXISTENT, sw2p1.getVersion());
                assertEquals(DPID2, sw2p1.getDpid());
                assertEquals(SW2_PORTNO1, sw2p1.getNumber());
                assertEquals(KVPort.STATUS.ACTIVE, sw2p1.getStatus());
            } catch (ObjectDoesntExistException | WrongVersionException e) {
                e.printStackTrace();
                fail("Port update failed " + e);
            }
        } catch (ObjectExistsException e) {
            e.printStackTrace();
            fail("Link creation failed " + e);
        }
    }


    private static void topology_walk() {
        Iterable<KVSwitch> swIt = KVSwitch.getAllSwitches();
        List<Long> switchesExpected = new ArrayList<>(Arrays.asList(DPID1, DPID2));

        System.out.println("Enumerating Switches start");
        for (KVSwitch sw : swIt) {
            System.out.println(sw + " @ " + sw.getVersion());
            assertNotEquals(VERSION_NONEXISTENT, sw.getVersion());
            assertEquals(KVSwitch.STATUS.ACTIVE, sw.getStatus());
            assertThat(sw.getDpid(), is(anyOf(equalTo(DPID1), equalTo(DPID2))));
            assertThat(switchesExpected, hasItem(sw.getDpid()));
            switchesExpected.remove(sw.getDpid());
        }
        System.out.println("Enumerating Switches end");

        KVSwitch sw1 = new KVSwitch(DPID1);
        try {
            sw1.read();
            assertNotEquals(VERSION_NONEXISTENT, sw1.getVersion());
            assertEquals(DPID1, sw1.getDpid());
            assertEquals(KVSwitch.STATUS.ACTIVE, sw1.getStatus());
        } catch (ObjectDoesntExistException e) {
            e.printStackTrace();
            fail("Reading switch failed " + e);
        }

        KVSwitch sw2 = new KVSwitch(DPID2);
        if (KVObject.multiRead(Arrays.asList(sw2))) {
            fail("Failed to read switch " + sw2);
        } else {
            assertNotEquals(VERSION_NONEXISTENT, sw2.getVersion());
            assertEquals(DPID2, sw2.getDpid());
            assertEquals(KVSwitch.STATUS.ACTIVE, sw2.getStatus());
        }


        // DPID -> [port_no]
        @SuppressWarnings("serial")
        Map<Long, List<Long>> expectedPorts = new HashMap<Long, List<Long>>() {{
            put(DPID1, new ArrayList<>(Arrays.asList(SW1_PORTNO1, SW1_PORTNO2)));
            put(DPID2, new ArrayList<>(Arrays.asList(SW2_PORTNO1, SW2_PORTNO2)));
        }};

        for (KVPort port : KVPort.getAllPorts()) {
            System.out.println(port + " @ " + port.getVersion());
            assertNotEquals(VERSION_NONEXISTENT, port.getVersion());
            assertEquals(KVPort.STATUS.ACTIVE, port.getStatus());
            assertThat(port.getDpid(), is(anyOf(equalTo(DPID1), equalTo(DPID2))));
            assertThat(port.getNumber(), is(anyOf(equalTo(SW1_PORTNO1), equalTo(SW1_PORTNO2))));

            assertThat(expectedPorts, hasKey(port.getDpid()));
            assertThat(expectedPorts.get(port.getDpid()), hasItem(port.getNumber()));
            expectedPorts.get(port.getDpid()).remove(port.getNumber());
        }

        // DeviceID -> PortID
        @SuppressWarnings("serial")
        Map<byte[], byte[]> expectedDevice = new TreeMap<byte[], byte[]>(ByteArrayComparator.BYTEARRAY_COMPARATOR) {{
            put(DEVICE1_MAC_SW1P1, KVPort.getPortID(DPID1, SW1_PORTNO1));
            put(DEVICE2_MAC_SW2P2, KVPort.getPortID(DPID2, SW2_PORTNO2));
        }};

        for (KVDevice device : KVDevice.getAllDevices()) {
            System.out.println(device + " @ " + device.getVersion());
            assertNotEquals(VERSION_NONEXISTENT, device.getVersion());

            assertThat(expectedDevice, hasKey(device.getMac()));
            assertThat(device.getAllPortIds(), hasItem(expectedDevice.get(device.getMac())));
            expectedDevice.remove(device.getMac());
        }

        for (KVLink link : KVLink.getAllLinks()) {
            System.out.println(link + " @ " + link.getVersion());
            assertNotEquals(VERSION_NONEXISTENT, link.getVersion());

            // there is currently only 1 link SW1P2->SW2P1
            assertEquals(DPID1, link.getSrc().dpid);
            assertEquals(SW1_PORTNO2, link.getSrc().number);
            assertEquals(DPID2, link.getDst().dpid);
            assertEquals(SW2_PORTNO1, link.getDst().number);
        }

    }


    private static void topology_delete() {

        for (KVSwitch sw : KVSwitch.getAllSwitches()) {
            try {
                sw.read();
                sw.delete();
                assertNotEquals(VERSION_NONEXISTENT, sw.getVersion());
            } catch (ObjectDoesntExistException | WrongVersionException e) {
                e.printStackTrace();
                fail("Delete Switch Failed " + e);
            }
        }

        for (KVPort p : KVPort.getAllPorts()) {
            try {
                p.read();
                p.delete();
                assertNotEquals(VERSION_NONEXISTENT, p.getVersion());
            } catch (ObjectDoesntExistException | WrongVersionException e) {
                e.printStackTrace();
                fail("Delete Port Failed " + e);
            }
        }

        for (KVDevice d : KVDevice.getAllDevices()) {
            d.forceDelete();
            assertNotEquals(VERSION_NONEXISTENT, d.getVersion());
        }

        for (KVLink l : KVLink.getAllLinks()) {
            try {
                l.read();
                l.delete();
                assertNotEquals(VERSION_NONEXISTENT, l.getVersion());
            } catch (ObjectDoesntExistException | WrongVersionException e) {
                e.printStackTrace();
                fail("Delete Link Failed " + e);
            }
        }
    }

    public static void main(final String[] argv) {

        topology_setup();
        topology_walk();
        topology_delete();

        System.exit(0);
    }

}
