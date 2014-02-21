/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.onrc.onos.datagrid.web;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.intent.ConstrainedShortestPathIntent;
import net.onrc.onos.intent.ShortestPathIntent;
import net.onrc.onos.intent.IntentOperation;
import net.onrc.onos.intent.IntentMap;
import net.onrc.onos.intent.Intent;
import net.onrc.onos.intent.runtime.IPathCalcRuntimeService;
import net.onrc.onos.intent.IntentOperationList;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.codehaus.jackson.map.ObjectMapper;
import net.floodlightcontroller.util.MACAddress;
import java.util.HashMap;
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
        IPathCalcRuntimeService pathRuntime = (IPathCalcRuntimeService)getContext()
                .getAttributes().get(IPathCalcRuntimeService.class.getCanonicalName());
        if (pathRuntime == null) {
            log.debug("Failed to get path calc runtime");
            System.out.println("Failed to get path calc runtime");
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
	    reply = parseJsonNode(jNode.getElements(), pathRuntime);
	}
        return reply;
    }

    @Get("json")
    public String retrieve() throws IOException {
        IPathCalcRuntimeService pathRuntime = (IPathCalcRuntimeService)getContext().
                getAttributes().get(IPathCalcRuntimeService.class.getCanonicalName());
        ObjectMapper mapper = new ObjectMapper();
        String restStr = "";

        String intentId = (String) getRequestAttributes().get("intent_id");
        ArrayNode arrayNode = mapper.createArrayNode();
        IntentMap intentMap = pathRuntime.getHighLevelIntents();
        Collection<Intent> intents = intentMap.getAllIntents();
        if (!intents.isEmpty()) {
            if ((intentId != null )) {
                Intent intent = intentMap.getIntent(intentId);
                if (intent != null) {
                    ObjectNode node = mapper.createObjectNode();
                    node.put("intent_id", intent.getId());
                    node.put("status", intent.getState().toString());
                    arrayNode.add(node);
                }
            } else {
                for (Intent intent : intents) {
                    ObjectNode node = mapper.createObjectNode();
                    node.put("intent_id", intent.getId());
                    node.put("status", intent.getState().toString());
                    arrayNode.add(node);
                }
            }
            restStr = mapper.writeValueAsString(arrayNode);
        }
        return restStr;
    }
    
    private String parseJsonNode(Iterator<JsonNode> nodes,
	    IPathCalcRuntimeService pathRuntime) throws IOException {
        IntentOperationList operations = new IntentOperationList();
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
	    }
	}
        pathRuntime.executeIntentOperations(operations);
        return mapper.writeValueAsString(arrayNode);
    }

    private void appendIntentStatus(String status, final String applnIntentId, 
            ObjectMapper mapper, ArrayNode arrayNode) throws IOException {
        ObjectNode node = mapper.createObjectNode();
        node.put("intent_id", applnIntentId);
        node.put("status", status);
        arrayNode.add(node);
    }
    
    private String processIntent(Map<String, Object> fields, IntentOperationList operations) {
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
}
