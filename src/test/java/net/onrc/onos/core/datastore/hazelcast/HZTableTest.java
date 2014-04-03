package net.onrc.onos.core.datastore.hazelcast;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

import net.onrc.onos.core.datastore.IKVTable.IKVEntry;
import net.onrc.onos.core.datastore.ObjectDoesntExistException;
import net.onrc.onos.core.datastore.ObjectExistsException;
import net.onrc.onos.core.datastore.WrongVersionException;
import net.onrc.onos.core.datastore.hazelcast.HZTable.VersionedValue;
import net.onrc.onos.core.datastore.utils.ByteArrayComparator;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class HZTableTest {
    @Rule
    public TestName name = new TestName();

    static final String TEST_TABLE_NAME = "TableForUnitTest";
    HZTable table;

    @Before
    public void setUp() throws Exception {
        table = (HZTable) HZClient.getClient().getTable(TEST_TABLE_NAME);
    }

    @After
    public void tearDown() throws Exception {
        HZClient.getClient().dropTable(table);
    }

    public void assertEntryInTable(final byte[] key, final byte[] value, final long version) {
        VersionedValue valueblob = table.getBackendMap().get(key);
        assertNotNull(valueblob);
        assertArrayEquals(value, valueblob.getValue());
        assertEquals(version, valueblob.getVersion());
    }

    public void assertKeyNotInTable(final byte[] key) {
        VersionedValue valueblob = table.getBackendMap().get(key);
        assertNull(valueblob);
    }

    @Test
    public void testGetInitialVersion() {
        final long version1 = HZTable.getInitialVersion();
        assertNotEquals(HZClient.VERSION_NONEXISTENT, version1);

        final long version2 = HZTable.getInitialVersion();
        assertNotEquals(HZClient.VERSION_NONEXISTENT, version2);
        assertNotEquals(version1, version2);
    }

    @Test
    public void testGetNextVersion() {
        final long nextVersion = HZTable.getNextVersion(1);
        assertNotEquals(nextVersion, HZClient.VERSION_NONEXISTENT);
        assertNotEquals(nextVersion, 1);

        final long nextVersion1 = HZTable.getNextVersion(Long.MAX_VALUE);
        assertNotEquals(nextVersion1, HZClient.VERSION_NONEXISTENT);
        assertNotEquals(nextVersion1, Long.MAX_VALUE);

        final long nextVersion11 = HZTable.getNextVersion(HZClient.VERSION_NONEXISTENT - 1);
        assertNotEquals(nextVersion11, HZClient.VERSION_NONEXISTENT);
        assertNotEquals(nextVersion11, HZClient.VERSION_NONEXISTENT - 1);
    }

    @Ignore // nothing to test for now
    @Test
    public void testHZTable() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetTableName() {
        assertEquals(TEST_TABLE_NAME, table.getTableName());
    }

    @Test
    public void testVERSION_NONEXISTENT() {
        assertEquals(HZClient.VERSION_NONEXISTENT, table.VERSION_NONEXISTENT());
    }

    @Test
    public void testGetTableId() {
        // for Hazelcast implementation IKVTableID is table itself
        assertEquals(table, table.getTableId());
    }

    @Test
    public void testCreate() throws ObjectExistsException {
        final byte[] key = name.getMethodName().getBytes(StandardCharsets.UTF_8);
        final byte[] value = "SomeValue".getBytes(StandardCharsets.UTF_8);

        final long version = table.create(key, value);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, version);

        assertEntryInTable(key, value, version);
    }

    @Test(expected = ObjectExistsException.class)
    public void testCreateConflict() throws ObjectExistsException {
        final byte[] key = name.getMethodName().getBytes(StandardCharsets.UTF_8);
        final byte[] value = "SomeValue".getBytes(StandardCharsets.UTF_8);

        final long version = table.create(key, value);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, version);

        assertEntryInTable(key, value, version);

        table.create(key, value);
        fail("Should have thrown exception");
    }

    @Test
    public void testForceCreate() {
        final byte[] key = name.getMethodName().getBytes(StandardCharsets.UTF_8);
        final byte[] value = "SomeValue".getBytes(StandardCharsets.UTF_8);

        final long version = table.forceCreate(key, value);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, version);
        assertEntryInTable(key, value, version);


        final long version1 = table.forceCreate(key, value);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, version1);
        assertNotEquals(version, version1);
        assertEntryInTable(key, value, version1);
    }

    @Test
    public void testRead() throws ObjectDoesntExistException {
        final byte[] key = name.getMethodName().getBytes(StandardCharsets.UTF_8);
        final byte[] value = "SomeValue".getBytes(StandardCharsets.UTF_8);

        // put data to read
        final long version = table.forceCreate(key, value);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, version);
        assertEntryInTable(key, value, version);

        // test body
        final IKVEntry readValue = table.read(key);
        assertArrayEquals(key, readValue.getKey());
        assertArrayEquals(value, readValue.getValue());
        assertEquals(version, readValue.getVersion());
    }


    @Test(expected = ObjectDoesntExistException.class)
    public void testReadNotExist() throws ObjectDoesntExistException {
        final byte[] key = name.getMethodName().getBytes(StandardCharsets.UTF_8);

        table.read(key);
        fail("Should have thrown exception");
    }

    @Test
    public void testUpdateByteArrayByteArrayLongSuccess() throws ObjectDoesntExistException, WrongVersionException {
        final byte[] key = name.getMethodName().getBytes(StandardCharsets.UTF_8);
        final byte[] oldValue = "OldValue".getBytes(StandardCharsets.UTF_8);
        final byte[] value = "SomeValue".getBytes(StandardCharsets.UTF_8);

        // put data to update
        final long oldVersion = table.forceCreate(key, oldValue);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, oldVersion);
        assertEntryInTable(key, oldValue, oldVersion);

        final long version = table.update(key, value, oldVersion);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, version);
        assertEntryInTable(key, value, version);
    }

    @Test(expected = ObjectDoesntExistException.class)
    public void testUpdateByteArrayByteArrayLongFailNoOldValue() throws ObjectDoesntExistException, WrongVersionException {
        final byte[] key = name.getMethodName().getBytes(StandardCharsets.UTF_8);
        final byte[] value = "SomeValue".getBytes(StandardCharsets.UTF_8);

        final long oldVersion = 0xDEAD;

        final long version = table.update(key, value, oldVersion);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, version);
        assertEntryInTable(key, value, version);
    }

    @Test(expected = WrongVersionException.class)
    public void testUpdateByteArrayByteArrayLongFailWrongVersion() throws ObjectDoesntExistException, WrongVersionException {
        final byte[] key = name.getMethodName().getBytes(StandardCharsets.UTF_8);
        final byte[] oldValue = "OldValue".getBytes(StandardCharsets.UTF_8);
        final byte[] value = "SomeValue".getBytes(StandardCharsets.UTF_8);

        // put data to update
        final long oldVersion = table.forceCreate(key, oldValue);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, oldVersion);
        assertEntryInTable(key, oldValue, oldVersion);
        // some one updates (from different thread/process in reality)
        table.forceCreate(key, oldValue);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, oldVersion);


        table.update(key, value, oldVersion);
        fail("Should have thrown exception");
    }

    @Test
    public void testUpdateByteArrayByteArraySuccess() throws ObjectDoesntExistException {
        final byte[] key = name.getMethodName().getBytes(StandardCharsets.UTF_8);
        final byte[] oldValue = "OldValue".getBytes(StandardCharsets.UTF_8);
        final byte[] value = "SomeValue".getBytes(StandardCharsets.UTF_8);

        // put data to update
        final long oldVersion = table.forceCreate(key, oldValue);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, oldVersion);
        assertEntryInTable(key, oldValue, oldVersion);

        final long version = table.update(key, value);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, version);
        assertEntryInTable(key, value, version);
    }

    @Test(expected = ObjectDoesntExistException.class)
    public void testUpdateByteArrayByteArrayFailNoOldValue() throws ObjectDoesntExistException, WrongVersionException {
        final byte[] key = name.getMethodName().getBytes(StandardCharsets.UTF_8);
        final byte[] value = "SomeValue".getBytes(StandardCharsets.UTF_8);

        final long version = table.update(key, value);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, version);
        assertEntryInTable(key, value, version);
        fail("Should have thrown exception");
    }

    @Test
    public void testUpdateByteArrayByteArraySuccessIgnoreVersion() throws ObjectDoesntExistException, WrongVersionException {
        final byte[] key = name.getMethodName().getBytes(StandardCharsets.UTF_8);
        final byte[] oldValue = "OldValue".getBytes(StandardCharsets.UTF_8);
        final byte[] value = "SomeValue".getBytes(StandardCharsets.UTF_8);

        // put data to update
        final long oldVersion = table.forceCreate(key, oldValue);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, oldVersion);
        assertEntryInTable(key, oldValue, oldVersion);
        // someone updates (from different thread/process in reality)
        table.forceCreate(key, oldValue);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, oldVersion);


        final long version = table.update(key, value);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, version);
        assertEntryInTable(key, value, version);
    }

    @Test
    public void testDelete() throws ObjectDoesntExistException, WrongVersionException {
        final byte[] key = name.getMethodName().getBytes(StandardCharsets.UTF_8);
        final byte[] oldValue = "OldValue".getBytes(StandardCharsets.UTF_8);

        // put data to delete
        final long oldVersion = table.forceCreate(key, oldValue);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, oldVersion);
        assertEntryInTable(key, oldValue, oldVersion);

        long version = table.delete(key, oldVersion);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, oldVersion);
        assertEquals(oldVersion, version);
        assertKeyNotInTable(key);
    }

    @Test(expected = ObjectDoesntExistException.class)
    public void testDeleteFailNoEntry() throws ObjectDoesntExistException, WrongVersionException {
        final byte[] key = name.getMethodName().getBytes(StandardCharsets.UTF_8);

        final long oldVersion = 0xDEAD;

        try {
            table.delete(key, oldVersion);
        } catch (ObjectDoesntExistException | WrongVersionException e) {
            assertKeyNotInTable(key);
            throw e;
        }
        fail("Should have thrown exception");
    }

    @Test(expected = WrongVersionException.class)
    public void testDeleteFailWrongVersion() throws ObjectDoesntExistException, WrongVersionException {
        final byte[] key = name.getMethodName().getBytes(StandardCharsets.UTF_8);
        final byte[] oldValue = "OldValue".getBytes(StandardCharsets.UTF_8);

        // put data to delete
        final long oldVersion = table.forceCreate(key, oldValue);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, oldVersion);
        assertEntryInTable(key, oldValue, oldVersion);
        // someone updates (from different thread/process in reality)
        final long latestVersion = table.forceCreate(key, oldValue);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, latestVersion);

        try {
            table.delete(key, oldVersion);
        } catch (ObjectDoesntExistException | WrongVersionException e) {
            assertEntryInTable(key, oldValue, latestVersion);
            throw e;
        }
        fail("Should have thrown exception");
    }


    @Test
    public void testForceDelete() {
        final byte[] key = name.getMethodName().getBytes(StandardCharsets.UTF_8);
        final byte[] oldValue = "OldValue".getBytes(StandardCharsets.UTF_8);

        // put data to delete
        final long oldVersion = table.forceCreate(key, oldValue);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, oldVersion);
        assertEntryInTable(key, oldValue, oldVersion);

        long version = table.forceDelete(key);
        assertNotEquals(HZClient.VERSION_NONEXISTENT, oldVersion);
        assertEquals(oldVersion, version);
        assertKeyNotInTable(key);
    }

    @Test
    public void testGetAllEntries() {
        final int DATASETSIZE = 100;
        final Map<byte[], VersionedValue> testdata = new TreeMap<>(ByteArrayComparator.BYTEARRAY_COMPARATOR);
        for (int i = 0; i < DATASETSIZE; ++i) {
            final byte[] key = (name.getMethodName() + i).getBytes(StandardCharsets.UTF_8);
            final byte[] value = ("Value" + i).getBytes(StandardCharsets.UTF_8);

            // put data to delete
            final long version = table.forceCreate(key, value);
            assertNotEquals(HZClient.VERSION_NONEXISTENT, version);
            assertEntryInTable(key, value, version);

            testdata.put(key, new VersionedValue(value, version));
        }

        Iterable<IKVEntry> datastore = table.getAllEntries();
        for (IKVEntry entry : datastore) {
            VersionedValue expectedValue = testdata.get(entry.getKey());
            assertNotNull(expectedValue);
            assertArrayEquals(expectedValue.getValue(), entry.getValue());
            assertEquals(expectedValue.getVersion(), entry.getVersion());

            testdata.remove(entry.getKey());
        }

        assertTrue(testdata.isEmpty());
    }

    @Test
    public void testToString() {
        assertEquals("[HZTable " + TEST_TABLE_NAME + "]", table.toString());
    }

}
