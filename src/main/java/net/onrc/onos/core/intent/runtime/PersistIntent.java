/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.onrc.onos.core.intent.runtime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import net.onrc.onos.core.datagrid.web.IntentResource;
import net.onrc.onos.core.datastore.DataStoreClient;
import net.onrc.onos.core.datastore.IKVTable;
import net.onrc.onos.core.datastore.ObjectExistsException;
import net.onrc.onos.core.intent.IntentOperationList;
import net.onrc.onos.core.registry.IControllerRegistryService;
import net.onrc.onos.core.registry.IdBlock;
import net.onrc.onos.core.util.serializers.KryoFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

/**
 * @author nickkaranatsios
 */
public class PersistIntent {
    private static final Logger log = LoggerFactory.getLogger(IntentResource.class);
    private long range = 10000L;
    private final IControllerRegistryService controllerRegistry;
    private static final String INTENT_JOURNAL = "G:IntentJournal";
    private static final int VALUE_STORE_LIMIT = 1024 * 1024;
    private IKVTable table;
    private Kryo kryo;
    private ByteArrayOutputStream stream;
    private Output output = null;
    private AtomicLong nextId = null;
    private long rangeEnd;
    private IdBlock idBlock = null;


    public PersistIntent(final IControllerRegistryService controllerRegistry) {
        this.controllerRegistry = controllerRegistry;
        table = DataStoreClient.getClient().getTable(INTENT_JOURNAL);
        stream = new ByteArrayOutputStream(1024);
        output = new Output(stream);
        kryo = (new KryoFactory()).newKryo();
    }

    public long getKey() {
        long key;
        if (idBlock == null) {
            key = getNextBlock();
        } else {
            key = nextId.incrementAndGet();
            if (key >= rangeEnd) {
                key = getNextBlock();
            }
        }
        return key;
    }

    private long getNextBlock() {
        // XXX This method is not thread safe, may lose allocated IdBlock
        idBlock = controllerRegistry.allocateUniqueIdBlock(range);
        nextId = new AtomicLong(idBlock.getStart());
        rangeEnd = idBlock.getEnd();
        return nextId.get();
    }

    public boolean persistIfLeader(long key, IntentOperationList operations) {
        boolean leader = true;
        boolean ret = false;
        // TODO call controllerRegistry.isClusterLeader()
        if (leader) {
            try {
                // reserve key 10 entries for multi-write if size over 1MB
                key *= 10;
                kryo.writeObject(output, operations);
                output.close();
                ByteBuffer keyBytes = ByteBuffer.allocate(8).putLong(key);
                byte[] buffer = stream.toByteArray();
                int total = buffer.length;
                if ((total >= VALUE_STORE_LIMIT)) {
                    int writeCount = total / VALUE_STORE_LIMIT;
                    int remainder = total % VALUE_STORE_LIMIT;
                    int upperIndex = 0;
                    for (int i = 0; i < writeCount; i++, key++) {
                        keyBytes.clear();
                        keyBytes.putLong(key);
                        keyBytes.flip();
                        upperIndex = (i * VALUE_STORE_LIMIT + VALUE_STORE_LIMIT) - 1;
                        log.debug("writing using indexes {}:{}", (i * VALUE_STORE_LIMIT), upperIndex);
                        table.create(keyBytes.array(), Arrays.copyOfRange(buffer, i * VALUE_STORE_LIMIT, upperIndex));
                    }
                    if (remainder > 0) {
                        keyBytes.clear();
                        keyBytes.putLong(key);
                        keyBytes.flip();
                        log.debug("writing using indexes {}:{}", upperIndex, total);
                        table.create(keyBytes.array(), Arrays.copyOfRange(buffer, upperIndex + 1, total - 1));
                    }
                } else {
                    keyBytes.flip();
                    table.create(keyBytes.array(), buffer);
                }
                log.debug("key is {} value length is {}", key, buffer.length);
                stream.reset();
                stream.close();
                log.debug("persist operations to ramcloud size of operations: {}", operations.size());
                ret = true;
            } catch (ObjectExistsException ex) {
                log.warn("Failed to store intent journal with key " + key);
            } catch (IOException ex) {
                log.error("Failed to close the stream");
            }
        }
        return ret;
    }
}
