/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.onrc.onos.intent.persist;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import edu.stanford.ramcloud.JRamCloud;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.onrc.onos.datagrid.web.IntentResource;
import net.onrc.onos.datastore.RCTable;
import net.onrc.onos.intent.IntentOperationList;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.registry.controller.IControllerRegistryService;
import net.onrc.onos.registry.controller.IdBlock;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nickkaranatsios
 */
public class PersistIntent {
    private final static org.slf4j.Logger log = LoggerFactory.getLogger(IntentResource.class);
    private final static long range = 10000;
    private final IControllerRegistryService controllerRegistry;
    NetworkGraph graph = null;
    private final String intentJournal = "G:IntentJournal";
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
        stream = new ByteArrayOutputStream();
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
                System.out.println("persist operations to ramcloud");
                kryo.writeObject(output, operations);
                output.close();
                byte[] buffer = stream.toByteArray();
                table.create(String.valueOf(key).getBytes(), buffer);
                System.out.println("key is " + key);
                ret = true;
            } catch (JRamCloud.ObjectExistsException ex) {
                log.warn("Failed to store intent journal with key " + key);
            }
        }
        return ret;
    }
}
