package net.onrc.onos.datastore.ramcloud;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.onrc.onos.datastore.IKVClient;
import net.onrc.onos.datastore.IKVTable;
import net.onrc.onos.datastore.IKVTable.IKVEntry;
import net.onrc.onos.datastore.IKVTableID;
import net.onrc.onos.datastore.IMultiEntryOperation;
import net.onrc.onos.datastore.IMultiEntryOperation.STATUS;
import net.onrc.onos.datastore.ObjectDoesntExistException;
import net.onrc.onos.datastore.ObjectExistsException;
import net.onrc.onos.datastore.WrongVersionException;
import net.onrc.onos.datastore.internal.IModifiableMultiEntryOperation;
import net.onrc.onos.datastore.ramcloud.RCTable.Entry;
import net.onrc.onos.datastore.utils.ByteArrayUtil;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.ramcloud.JRamCloud;
import edu.stanford.ramcloud.JRamCloud.MultiReadObject;
import edu.stanford.ramcloud.JRamCloud.MultiWriteObject;
import edu.stanford.ramcloud.JRamCloud.MultiWriteRspObject;
import edu.stanford.ramcloud.JRamCloud.RejectRules;
import edu.stanford.ramcloud.JRamCloud.RejectRulesException;
import edu.stanford.ramcloud.JRamCloud.TableEnumerator2;

public class RCClient implements IKVClient {
    private static final Logger log = LoggerFactory.getLogger(RCClient.class);

    private static final String DB_CONFIG_FILE = "conf/ramcloud.conf";
    public static final Configuration config = getConfiguration();

    // Value taken from RAMCloud's Status.h
    // FIXME These constants should be defined by JRamCloud
    public static final int STATUS_OK = 0;

    // FIXME come up with a proper way to retrieve configuration
    public static final int MAX_MULTI_READS = Math.max(1, Integer
            .valueOf(System.getProperty("ramcloud.max_multi_reads", "400")));

    public static final int MAX_MULTI_WRITES = Math.max(1, Integer
            .valueOf(System.getProperty("ramcloud.max_multi_writes", "800")));

    private static final ThreadLocal<JRamCloud> tlsRCClient = new ThreadLocal<JRamCloud>() {
        @Override
        protected JRamCloud initialValue() {
            return new JRamCloud(getCoordinatorUrl(config));
        }
    };

    /**
     * @return JRamCloud instance intended to be used only within the
     *         SameThread.
     * @note Do not store the returned instance in a member variable, etc. which
     *       may be accessed later by another thread.
     */
    static JRamCloud getJRamCloudClient() {
        return tlsRCClient.get();
    }

    // Currently RCClient is state-less
    private static final RCClient theInstance= new RCClient();

    public static RCClient getClient() {
        return theInstance;
    }

    public static final Configuration getConfiguration() {
        final File configFile = new File(System.getProperty("ramcloud.config.path", DB_CONFIG_FILE));
        return getConfiguration(configFile);
    }

    public static final Configuration getConfiguration(final File configFile) {
        if (configFile == null) {
            throw new IllegalArgumentException("Need to specify a configuration file or storage directory");
        }

        if (!configFile.isFile()) {
            throw new IllegalArgumentException("Location of configuration must be a file");
        }

        try {
            return new PropertiesConfiguration(configFile);
        } catch (ConfigurationException e) {
            throw new IllegalArgumentException("Could not load configuration at: " + configFile, e);
        }
    }

    public static String getCoordinatorUrl(final Configuration configuration) {
        final String coordinatorIp = configuration.getString("ramcloud.coordinatorIp", "fast+udp:host=127.0.0.1");
        final String coordinatorPort = configuration.getString("ramcloud.coordinatorPort", "port=12246");
        final String coordinatorURL = coordinatorIp + "," + coordinatorPort;
        return coordinatorURL;
    }

    @Override
    public IMultiEntryOperation createOp(IKVTableID tableId, byte[] key, byte[] value) {
        return RCMultiEntryOperation.create(tableId, key, value);
    }

    /**
     * @param tableId RCTableID instance
     */
    @Override
    public long create(IKVTableID tableId, byte[] key, byte[] value)
            throws ObjectExistsException {

        RCTableID rcTableId = (RCTableID) tableId;
        JRamCloud rcClient = RCClient.getJRamCloudClient();

        RejectRules rules = new RejectRules();
        rules.rejectIfExists();

        try {
            return rcClient.write(rcTableId.getTableID(), key, value, rules);
        } catch (JRamCloud.ObjectExistsException e) {
            throw new ObjectExistsException(rcTableId, key, e);
        } catch (JRamCloud.RejectRulesException e) {
            log.error("Unexpected RejectRulesException", e);
            return JRamCloud.VERSION_NONEXISTENT;
        }
    }

    @Override
    public IMultiEntryOperation forceCreateOp(IKVTableID tableId, byte[] key, byte[] value) {
        return RCMultiEntryOperation.forceCreate(tableId, key, value);
    }

    @Override
    public long forceCreate(IKVTableID tableId, byte[] key, byte[] value) {
        RCTableID rcTableId = (RCTableID) tableId;
        JRamCloud rcClient = RCClient.getJRamCloudClient();

        long updated_version = rcClient.write(rcTableId.getTableID(), key, value);
        return updated_version;
    }

    @Override
    public IMultiEntryOperation readOp(IKVTableID tableId, byte[] key) {
        return RCMultiEntryOperation.read(tableId, key);
    }

    @Override
    public IKVEntry read(IKVTableID tableId, byte[] key)
            throws ObjectDoesntExistException {

        RCTableID rcTableId = (RCTableID) tableId;
        JRamCloud rcClient = RCClient.getJRamCloudClient();

        RejectRules rules = new RejectRules();
        rules.rejectIfDoesntExists();
        try {
            JRamCloud.Object rcObj = rcClient.read(rcTableId.getTableID(), key, rules);
            return new Entry(rcObj.key, rcObj.value, rcObj.version);
        } catch (JRamCloud.ObjectDoesntExistException e) {
            throw new ObjectDoesntExistException(rcTableId, key, e);
        } catch (JRamCloud.RejectRulesException e) {
            log.error("Unexpected RejectRulesException", e);
            return null;
        }
    }

    @Override
    public IMultiEntryOperation updateOp(IKVTableID tableId, byte[] key, byte[] value,long version) {
        return RCMultiEntryOperation.update(tableId, key, value, version);
    }

    @Override
    public long update(IKVTableID tableId, byte[] key, byte[] value,
            long version) throws ObjectDoesntExistException,
            WrongVersionException {

        RCTableID rcTableId = (RCTableID) tableId;
        JRamCloud rcClient = RCClient.getJRamCloudClient();

        RejectRules rules = new RejectRules();
        rules.rejectIfDoesntExists();
        rules.rejectIfNeVersion(version);

        try {
            return rcClient.write(rcTableId.getTableID(), key, value, rules);
        } catch (JRamCloud.ObjectDoesntExistException e) {
            throw new ObjectDoesntExistException(rcTableId, key, e);
        } catch (JRamCloud.WrongVersionException e) {
            throw new WrongVersionException(rcTableId, key, version, e);
        } catch (JRamCloud.RejectRulesException e) {
            log.error("Unexpected RejectRulesException", e);
            return JRamCloud.VERSION_NONEXISTENT;
        }
    }


    @Override
    public long update(IKVTableID tableId, byte[] key, byte[] value)
            throws ObjectDoesntExistException {

        RCTableID rcTableId = (RCTableID) tableId;
        JRamCloud rcClient = RCClient.getJRamCloudClient();

        RejectRules rules = new RejectRules();
        rules.rejectIfDoesntExists();

        try {
            return rcClient.write(rcTableId.getTableID(), key, value, rules);
        } catch (JRamCloud.ObjectDoesntExistException e) {
            throw new ObjectDoesntExistException(rcTableId, key, e);
        } catch (JRamCloud.RejectRulesException e) {
            log.error("Unexpected RejectRulesException", e);
            return JRamCloud.VERSION_NONEXISTENT;
        }
    }

    @Override
    public IMultiEntryOperation deleteOp(IKVTableID tableId, byte[] key, byte[] value,long version) {
        return RCMultiEntryOperation.delete(tableId, key, value, version);
    }

    @Override
    public long delete(IKVTableID tableId, byte[] key, long version)
            throws ObjectDoesntExistException, WrongVersionException {

        RCTableID rcTableId = (RCTableID) tableId;
        JRamCloud rcClient = RCClient.getJRamCloudClient();

        RejectRules rules = new RejectRules();
        rules.rejectIfDoesntExists();
        rules.rejectIfNeVersion(version);

        try {
            return rcClient.remove(rcTableId.getTableID(), key, rules);
        } catch (JRamCloud.ObjectDoesntExistException e) {
            throw new ObjectDoesntExistException(rcTableId, key, e);
        } catch (JRamCloud.WrongVersionException e) {
            throw new WrongVersionException(rcTableId, key, version, e);
        } catch (JRamCloud.RejectRulesException e) {
            log.error("Unexpected RejectRulesException", e);
            return JRamCloud.VERSION_NONEXISTENT;
        }
    }

    @Override
    public IMultiEntryOperation forceDeleteOp(IKVTableID tableId, byte[] key) {
        return RCMultiEntryOperation.forceDelete(tableId, key);
    }

    @Override
    public long forceDelete(IKVTableID tableId, byte[] key) {
        RCTableID rcTableId = (RCTableID) tableId;
        JRamCloud rcClient = RCClient.getJRamCloudClient();
        long removed_version = rcClient.remove(rcTableId.getTableID(), key);
        return removed_version;
    }

    @Override
    public Iterable<IKVEntry> getAllEntries(IKVTableID tableId) {
        return new RCTableEntryIterable((RCTableID) tableId);
    }

    static class RCTableEntryIterable implements Iterable<IKVEntry> {
        private final RCTableID tableId;

        public RCTableEntryIterable(final RCTableID tableId) {
            this.tableId = tableId;
        }

        @Override
        public Iterator<IKVEntry> iterator() {
            return new RCClient.RCTableIterator(tableId);
        }
    }

    public static class RCTableIterator implements Iterator<IKVEntry> {
        private final RCTableID tableId;
        protected final TableEnumerator2 enumerator;
        private JRamCloud.Object last;

        public RCTableIterator(final RCTableID tableId) {
            this.tableId = tableId;
            this.enumerator = getJRamCloudClient().new TableEnumerator2(tableId.getTableID());
            this.last = null;
        }

        @Override
        public boolean hasNext() {
            return this.enumerator.hasNext();
        }

        @Override
        public RCTable.Entry next() {
            last = enumerator.next();
            return new RCTable.Entry(last.key, last.value, last.version);
        }

        @Override
        public void remove() {
            if (last != null) {
                getJRamCloudClient();
                JRamCloud rcClient = RCClient.getJRamCloudClient();

                RejectRules rules = new RejectRules();
                rules.rejectIfNeVersion(last.version);
                try {
                    rcClient.remove(tableId.getTableID(), last.key, rules);
                } catch (RejectRulesException e) {
                    log.trace("remove failed", e);
                }
                last = null;
            }
        }
    }

    @Override
    public boolean multiRead(final Collection<IMultiEntryOperation> ops) {

        if ( ops.size() <= MAX_MULTI_READS && ops instanceof ArrayList) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            final ArrayList<RCMultiEntryOperation> arrays = (ArrayList)ops;
            return multiReadInternal(arrays);
        }

        boolean fail_exists = false;

        ArrayList<RCMultiEntryOperation> req = new ArrayList<>();
        Iterator<IMultiEntryOperation> it = ops.iterator();
        while (it.hasNext()) {

            req.add((RCMultiEntryOperation) it.next());

            if (req.size() >= MAX_MULTI_READS) {
                // dispatch multiRead
                fail_exists |= multiReadInternal(req);
                req.clear();
            }
        }

        if (!req.isEmpty()) {
            // dispatch multiRead
            fail_exists |= multiReadInternal(req);
            req.clear();
        }

        return fail_exists;
    }

    @Override
    public boolean multiWrite(final List<IMultiEntryOperation> ops) {

        if ( ops.size() <= MAX_MULTI_WRITES && ops instanceof ArrayList) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            final ArrayList<RCMultiEntryOperation> arrays = (ArrayList)ops;
            return multiWriteInternal(arrays);
        }

        boolean fail_exists = false;

        ArrayList<RCMultiEntryOperation> req = new ArrayList<>();
        Iterator<IMultiEntryOperation> it = ops.iterator();
        while (it.hasNext()) {

            req.add((RCMultiEntryOperation) it.next());

            if (req.size() >= MAX_MULTI_WRITES) {
                // dispatch multiWrite
                fail_exists |= multiWriteInternal(req);
                req.clear();
            }
        }

        if (!req.isEmpty()) {
            // dispatch multiWrite
            fail_exists |= multiWriteInternal(req);
            req.clear();
        }

        return fail_exists;
    }

    @Override
    public boolean multiDelete(final Collection<IMultiEntryOperation> ops) {

        // TODO implement multiRemove JNI, etc. if we need performance

        boolean fail_exists = false;
        JRamCloud rcClient = getJRamCloudClient();

        for (IMultiEntryOperation iop : ops) {
            RCMultiEntryOperation op = (RCMultiEntryOperation)iop;
            switch (op.getOperation()) {
            case DELETE:
                RejectRules rules = new RejectRules();
                rules.rejectIfDoesntExists();
                rules.rejectIfNeVersion(op.getVersion());

                try {
                    long removed_version = rcClient.remove(op.tableId.getTableID(), op.entry.getKey(), rules);
                    op.entry.setVersion(removed_version);
                    op.status = STATUS.SUCCESS;
                } catch (JRamCloud.ObjectDoesntExistException|JRamCloud.WrongVersionException e) {
                    log.error("Failed to remove key:" + ByteArrayUtil.toHexStringBuffer(op.entry.getKey(), "") + " from tableID:" + op.tableId, e );
                    fail_exists = true;
                    op.status = STATUS.FAILED;
                } catch (JRamCloud.RejectRulesException e) {
                    log.error("Failed to remove key:" + ByteArrayUtil.toHexStringBuffer(op.entry.getKey(), "") + " from tableID:" + op.tableId, e );
                    fail_exists = true;
                    op.status = STATUS.FAILED;
                }
                break;

            case FORCE_DELETE:
                long removed_version = rcClient.remove(op.tableId.getTableID(), op.entry.getKey());
                if (removed_version != JRamCloud.VERSION_NONEXISTENT) {
                    op.entry.setVersion(removed_version);
                    op.status = STATUS.SUCCESS;
                } else {
                    log.error("Failed to remove key:{} from tableID:{}", ByteArrayUtil.toHexStringBuffer(op.entry.getKey(), ""), op.tableId );
                    fail_exists = true;
                    op.status = STATUS.FAILED;
                }
                break;

            default:
                log.error("Invalid operation {} specified on multiDelete", op.getOperation() );
                fail_exists = true;
                op.status = STATUS.FAILED;
                break;
            }
        }
        return fail_exists;
    }

    private boolean multiReadInternal(final ArrayList<RCMultiEntryOperation> ops) {
        boolean fail_exists = false;
        JRamCloud rcClient = RCClient.getJRamCloudClient();

        final int reqs = ops.size();

        MultiReadObject multiReadObjects = new MultiReadObject(reqs);

        // setup multi-read operation objects
        for (int i = 0; i < reqs; ++i) {
            IMultiEntryOperation op = ops.get(i);
            multiReadObjects.setObject(i, ((RCTableID)op.getTableId()).getTableID(), op.getKey());
        }

        // execute
        JRamCloud.Object[] results = rcClient.multiRead(multiReadObjects.tableId, multiReadObjects.key, multiReadObjects.keyLength, reqs);
        if (results.length != reqs) {
            log.error("multiRead returned unexpected number of results. (requested:{}, returned:{})", reqs, results.length);
            fail_exists = true;
        }

        for (int i = 0; i < results.length; ++i) {
            IModifiableMultiEntryOperation op = ops.get(i);
            if (results[i] == null) {
                log.error("MultiRead error, skipping {}, {}", op.getTableId(), op);
                fail_exists = true;
                op.setStatus(STATUS.FAILED);
                continue;
            }
            assert (Arrays.equals(results[i].key, op.getKey()));

            op.setValue(results[i].value, results[i].version);
            if (results[i].version == JRamCloud.VERSION_NONEXISTENT) {
                fail_exists = true;
                op.setStatus(STATUS.FAILED);
            } else {
                op.setStatus(STATUS.SUCCESS);
            }
        }

        return fail_exists;
    }

    private boolean multiWriteInternal(final ArrayList<RCMultiEntryOperation> ops) {
        boolean fail_exists = false;
        JRamCloud rcClient = RCClient.getJRamCloudClient();

        final int reqs = ops.size();

        MultiWriteObject multiWriteObjects = new MultiWriteObject(reqs);

        for (int i = 0; i < reqs; ++i) {

            IModifiableMultiEntryOperation op = ops.get(i);
            RejectRules rules = new RejectRules();

            switch (op.getOperation()) {
            case CREATE:
                rules.rejectIfExists();
                break;
            case FORCE_CREATE:
                // no reject rule
                break;
            case UPDATE:
                rules.rejectIfDoesntExists();
                rules.rejectIfNeVersion(op.getVersion());
                break;

            default:
                log.error("Invalid operation {} specified on multiWriteInternal", op.getOperation() );
                fail_exists = true;
                op.setStatus(STATUS.FAILED);
                return fail_exists;
            }
            multiWriteObjects.setObject(i, ((RCTableID)op.getTableId()).getTableID(), op.getKey(), op.getValue(), rules);
        }

        MultiWriteRspObject[] results = rcClient.multiWrite(multiWriteObjects.tableId, multiWriteObjects.key, multiWriteObjects.keyLength, multiWriteObjects.value, multiWriteObjects.valueLength, ops.size(), multiWriteObjects.rules);
        if (results.length != reqs) {
            log.error("multiWrite returned unexpected number of results. (requested:{}, returned:{})", reqs, results.length);
            fail_exists = true;
        }

        for (int i = 0; i < results.length; ++i) {
            IModifiableMultiEntryOperation op = ops.get(i);

            if (results[i] != null
                    && results[i].getStatus() == RCClient.STATUS_OK) {
                op.setStatus(STATUS.SUCCESS);
                op.setVersion(results[i].getVersion());
            } else {
                op.setStatus(STATUS.FAILED);
                fail_exists = true;
            }
        }

        return fail_exists;
    }

    private static final ConcurrentHashMap<String, RCTable> tables = new ConcurrentHashMap<>();

    @Override
    public IKVTable getTable(final String tableName) {
        RCTable table = tables.get(tableName);
        if (table == null) {
            RCTable new_table = new RCTable(tableName);
            RCTable existing_table = tables
                    .putIfAbsent(tableName, new_table);
            if (existing_table != null) {
                return existing_table;
            } else {
                return new_table;
            }
        }
        return table;
    }

    @Override
    public void dropTable(IKVTable table) {
        JRamCloud rcClient = RCClient.getJRamCloudClient();
        rcClient.dropTable(table.getTableId().getTableName());
        tables.remove(table.getTableId().getTableName());
    }

    static final long VERSION_NONEXISTENT = JRamCloud.VERSION_NONEXISTENT;

    @Override
    public long VERSION_NONEXISTENT() {
        return VERSION_NONEXISTENT;
    }
}
