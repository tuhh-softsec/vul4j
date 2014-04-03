package net.onrc.onos.core.datastore.topology;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.ByteBuffer;

import net.onrc.onos.core.datastore.topology.KVSwitch.STATUS;

import org.junit.Test;

public class KVSwitchNoDataStoreTest {

    @Test
    public void testGetDpidFromKeyByteArray() {
        // reference bytes
        final byte[] key = KVSwitch.getSwitchID(0x1L);

        assertEquals(0x1L, KVSwitch.getDpidFromKey(key));
    }

    @Test
    public void testGetDpidFromKeyByteBuffer() {
        // reference bytes
        final ByteBuffer key = ByteBuffer.wrap(KVSwitch.getSwitchID(0x1L));

        assertEquals(0x1L, KVSwitch.getDpidFromKey(key));
    }

    @Test
    public void testCreateFromKeyByteArray() {
        // reference bytes
        Long dpid = Long.valueOf(0x1L);
        final byte[] key = KVSwitch.getSwitchID(dpid);

        KVSwitch sw = KVSwitch.createFromKey(key);
        assertNotNull(sw);
        assertEquals(dpid, sw.getDpid());
    }

    @Test
    public void testGetStatus() {
        KVSwitch sw = new KVSwitch(0x1L);

        assertEquals(STATUS.INACTIVE, sw.getStatus());
    }

    @Test
    public void testSetStatus() {
        KVSwitch sw = new KVSwitch(0x1L);
        assertEquals(STATUS.INACTIVE, sw.getStatus());

        sw.setStatus(STATUS.ACTIVE);
        assertEquals(STATUS.ACTIVE, sw.getStatus());
    }

    @Test
    public void testGetDpid() {
        Long dpid = 0x1L;
        KVSwitch sw = new KVSwitch(dpid);
        assertEquals(dpid, sw.getDpid());
    }

    @Test
    public void testGetId() {
        // reference bytes
        Long dpid = Long.valueOf(0x1L);
        final byte[] key = KVSwitch.getSwitchID(dpid);

        KVSwitch sw = KVSwitch.createFromKey(key);
        assertArrayEquals(key, sw.getId());
    }

    @Test
    public void testToString() {
        final String expected = "[" + "KVSwitch"
                + " 0x" + 1 + " STATUS:" + STATUS.INACTIVE + "]";

        Long dpid = 0x1L;
        KVSwitch sw = new KVSwitch(dpid);

        assertEquals(expected, sw.toString());
    }
}
