/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.onrc.onos.intent.persist;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import edu.stanford.ramcloud.JRamCloud;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import net.onrc.onos.datagrid.web.IntentResource;
import net.onrc.onos.datastore.RCTable;
import net.onrc.onos.intent.IntentOperationList;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.registry.controller.IControllerRegistryService;
import net.onrc.onos.registry.controller.IdBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nickkaranatsios
 */
public class PersistIntent {
    private final static Logger log = LoggerFactory.getLogger(IntentResource.class);
    private long range = 10000L;
    private final IControllerRegistryService controllerRegistry;
    NetworkGraph graph = null;
    private final static String intentJournal = "G:IntentJournal";
    private final static int valueStoreLimit = 1024 * 1024;
    private RCTable table;
    private Kryo kryo = new Kryo();
    private ByteArrayOutputStream stream;
    private Output output = null;
    private AtomicLong nextId = null;
    private long rangeEnd;
    private IdBlock idBlock = null;
    
    
    public PersistIntent(final IControllerRegistryService controllerRegistry, INetworkGraphService ng) {
        this.controllerRegistry = controllerRegistry;
        this.graph = ng.getNetworkGraph();
        table = RCTable.getTable(intentJournal);
        stream = new ByteArrayOutputStream(1024);
        output = new Output(stream);
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
                if ((total >= valueStoreLimit )) {
                    int writeCount = total / valueStoreLimit;
                    int remainder = total % valueStoreLimit;
                    int upperIndex = 0;
                    for (int i = 0; i < writeCount; i++, key++) {
                        keyBytes.clear();
                        keyBytes.putLong(key);
                        keyBytes.flip();
                        upperIndex = (i * valueStoreLimit + valueStoreLimit) - 1;
                        log.debug("writing using indexes {}:{}", (i*valueStoreLimit) ,upperIndex);
                        table.create(keyBytes.array(), Arrays.copyOfRange(buffer, i * valueStoreLimit, upperIndex));
                    }
                    if (remainder > 0) {
                        keyBytes.clear();
                        keyBytes.putLong(key);
                        keyBytes.flip();
                        log.debug("writing using indexes {}:{}" ,upperIndex ,total);
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
            } catch (JRamCloud.ObjectExistsException ex) {
                log.warn("Failed to store intent journal with key " + key);
            } catch (IOException ex) {
                log.error("Failed to close the stream");
            }
        }
        return ret;
    }
}
