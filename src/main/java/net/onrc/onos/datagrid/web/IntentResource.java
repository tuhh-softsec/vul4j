/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.onrc.onos.datagrid.web;

import java.io.IOException;
import java.util.Iterator;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.intent.ConstrainedShortestPathIntent;
import net.onrc.onos.intent.ShortestPathIntent;
import net.onrc.onos.intent.IntentOperation;
import net.onrc.onos.intent.IntentMap;
//import net.onrc.onos.intent.Intent.IntentState;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.registry.controller.IControllerRegistryService;
import net.onrc.onos.registry.controller.IdBlock;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.codehaus.jackson.map.ObjectMapper;
import net.floodlightcontroller.util.MACAddress;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author nickkaranatsios
 */
public class IntentResource extends ServerResource {
    private final static Logger log = LoggerFactory.getLogger(IntentResource.class);
    private final String sep = ":";
    private IdBlock idBlock = null;
    private long nextIdBlock = 0;

    private class IntentStatus {
        String intentId;
        String status;
        
        public IntentStatus() {}
        
        public IntentStatus(String intentId, String status) {
            this.intentId = intentId;
            this.status = status;
        }
        
        public String getIntentId() {
            return intentId;
        }
        
        public void setIntentId(String intentId) {
            this.intentId = intentId;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
    }
    
    @Post("json")
    public String store(String jsonIntent) throws IOException {
	IDatagridService datagridService = (IDatagridService) getContext()
		.getAttributes().get(IDatagridService.class.getCanonicalName());
	if (datagridService == null) {
	    log.debug("FlowIntentResource ONOS Datagrid Service not found");
	    return "";
	}
        String reply = "";
	ObjectMapper mapper = new ObjectMapper();
	JsonNode jNode = null;
	try {
	    jNode = mapper.readValue(jsonIntent, JsonNode.class);
	} catch (JsonGenerationException ex) {
	    log.error("JsonGeneration exception ", ex);
	} catch (JsonMappingException ex) {
	    log.error("JsonMappingException occurred", ex);
	} catch (IOException ex) {
	    log.error("IOException occurred", ex);
	}

	if (jNode != null) {
	    Kryo kryo = new Kryo();
	    reply = parseJsonNode(kryo, jNode.getElements(), datagridService);
	}
        return reply;
    }

    @Get("json")
    public String retrieve() {
        return "123";
    }
    
    private String parseJsonNode(Kryo kryo, Iterator<JsonNode> nodes,
	    IDatagridService datagridService) throws IOException {
        LinkedList<IntentOperation> operations = new LinkedList<>();
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();
	while (nodes.hasNext()) {
	    JsonNode node = nodes.next();
	    if (node.isObject()) {
		JsonNode data;
		Iterator<String> fieldNames = node.getFieldNames();
                Map<String, Object> fields = new HashMap<>();
		while (fieldNames.hasNext()) {
		    String fieldName = fieldNames.next();
		    data = node.get(fieldName);
                    parseFields(data, fieldName, fields);
		}
                String status = processIntent(fields, operations);
                appendIntentStatus(status, (String)fields.get("intent_id"), mapper, arrayNode);
		// datagridService.registerIntent(Long.toString(uuid),
		// sb.toString().getBytes());
	    }
	}
        IntentMap intents = new IntentMap();
//        intents.executeOperations(operations); // TODO use PathCalcRuntimeModule
        return mapper.writeValueAsString(arrayNode);
    }

    private void appendIntentStatus(String status, final String applnIntentId, 
            ObjectMapper mapper, ArrayNode arrayNode) throws IOException {
        System.out.println("status " + status);
        String intentId = applnIntentId.split(":")[1];
        ObjectNode node = mapper.createObjectNode();
        node.put("intent_id", intentId);
        node.put("status", status);
        arrayNode.add(node);
    }
    
    private String processIntent(Map<String, Object> fields, LinkedList<IntentOperation> operations) {
        String intentType = (String)fields.get("intent_type");
        String intentOp = (String)fields.get("intent_op");
        String status = null;
        
        IntentOperation.Operator operation = IntentOperation.Operator.ADD;
        if ((intentOp.equals("remove"))) {
            operation = IntentOperation.Operator.REMOVE;
        }
        if (intentType.equals("shortest_intent_type")) {
            ShortestPathIntent spi = new ShortestPathIntent((String) fields.get("intent_id"),
                    Long.decode((String) fields.get("srcSwitch")),
                    (long) fields.get("srcPort"),
                    MACAddress.valueOf((String) fields.get("srcMac")).toLong(),
                    Long.decode((String) fields.get("dstSwitch")),
                    (long) fields.get("dstPort"),
                    MACAddress.valueOf((String) fields.get("dstMac")).toLong());
            operations.add(new IntentOperation(operation, spi));
            System.out.println("intent operation " + operation.toString());
            status = (spi.getState()).toString();
        } else {
            ConstrainedShortestPathIntent cspi = new ConstrainedShortestPathIntent((String) fields.get("intent_id"),
                    Long.decode((String) fields.get("srcSwitch")),
                    (long) fields.get("srcPort"),
                    MACAddress.valueOf((String) fields.get("srcMac")).toLong(),
                    Long.decode((String) fields.get("dstSwitch")),
                    (long) fields.get("dstPort"),
                    MACAddress.valueOf((String) fields.get("dstMac")).toLong(),
                    (double) fields.get("bandwidth"));
            operations.add(new IntentOperation(operation, cspi));
            status = (cspi.getState()).toString();
        }
        return status;
    }

    private void parseFields(JsonNode node, String fieldName, Map<String, Object> fields) {
        if ((node.isTextual())) {
            fields.put(fieldName, node.getTextValue());
        } else if ((node.isInt())) {
            fields.put(fieldName, (long)node.getIntValue());
        } else if (node.isDouble()) {
            fields.put(fieldName, node.getDoubleValue());
        } else if ((node.isLong())) {
            fields.put(fieldName, node.getLongValue());
        }
    }
    
    @Deprecated
    private long setPathIntentId() {
	long uuid = 0;
	if (idBlock == null || nextIdBlock + 1 == idBlock.getSize()) {
	    IControllerRegistryService controllerRegistry = getControllerRegistry();
	    if (controllerRegistry != null) {
		idBlock = controllerRegistry.allocateUniqueIdBlock();
		nextIdBlock = idBlock.getStart();
		System.out.println("start block " + nextIdBlock + " end block "
			+ idBlock.getEnd() + " size " + idBlock.getSize());
	    }
	}
	if (idBlock != null) {
	    uuid = nextIdBlock;
	    nextIdBlock++;
	}
	return uuid;
    }

    @Deprecated
    private void setIntentType(final String intentType, StringBuilder sb) {
	String canonicalName = null;
	if (intentType.equals("shortest-path")) {
	    canonicalName = ShortestPathIntent.class.getCanonicalName();
	    sb.append(ShortestPathIntent.class.getCanonicalName());
	} else if (intentType.equals("constrained-shortest-path")) {
	    canonicalName = ShortestPathIntent.class.getCanonicalName();
	}
	sb.append(canonicalName);
	sb.append(sep);
    }

    private IControllerRegistryService getControllerRegistry() {
	return (IControllerRegistryService) getContext().getAttributes().get(
		IControllerRegistryService.class.getCanonicalName());
    }

    @Deprecated
    private byte[] toBytes(Kryo kryo, Object value) {
	Output output = new Output(1024);
        kryo.writeObject(output, value);
        output.close();
        return output.toBytes();
    }
}
