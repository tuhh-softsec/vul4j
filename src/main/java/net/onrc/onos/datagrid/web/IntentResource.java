/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.onrc.onos.datagrid.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.intent.ConstrainedShortestPathIntent;
import net.onrc.onos.intent.Intent;
import net.onrc.onos.intent.IntentDeserializer;
import net.onrc.onos.intent.ShortestPathIntent;
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
import org.slf4j.LoggerFactory;

/**
 *
 * @author nickkaranatsios
 */
public class IntentResource extends ServerResource {

    private final static org.slf4j.Logger log = LoggerFactory.getLogger(IntentResource.class);
    private IdBlock idBlock = null;
    private long nextIdBlock = 0;

    @Post("json")
    public void store(String jsonFlowIntent) {
        IDatagridService datagridService =
                (IDatagridService) getContext().getAttributes().
                get(IDatagridService.class.getCanonicalName());
        if (datagridService == null) {
            log.debug("FlowIntentResource ONOS Datagrid Service not found");
            return;
        }
        INetworkGraphService networkGraphService = (INetworkGraphService)getContext().getAttributes().
                get(INetworkGraphService.class.getCanonicalName());
        NetworkGraph graph = networkGraphService.getNetworkGraph();
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jNode = null;
        try {
            System.out.println("json string " + jsonFlowIntent);
            jNode = mapper.readValue(jsonFlowIntent, JsonNode.class);
        } catch (JsonGenerationException ex) {
            log.error("JsonGeneration exception ", ex);
        } catch (JsonMappingException ex) {
            log.error("JsonMappingException occurred", ex);
        } catch (IOException ex) {
            log.error("IOException occurred", ex);
        }

        List<Intent> intents = new ArrayList<>();
        if (jNode != null) {
            parseJsonNode(graph, jNode.getElements(), intents);
            // datagridService.registerIntent(intents);
        }
    }

    private void parseJsonNode(NetworkGraph graph, Iterator<JsonNode> nodes, List<Intent> intents) {
        StringBuilder sb = new StringBuilder();
        sb.ensureCapacity(256);
        IntentDeserializer intentDesializer = null;
        
        while (nodes.hasNext()) {
            JsonNode node = nodes.next();
            if (node.isObject()) {
                JsonNode data = null;
                Iterator<String> fieldNames = node.getFieldNames();
                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    data = node.get(fieldName);
                    if (fieldName.equals("type")) {
                        if (data != null) {
                            System.out.println("type is not null " + data.getTextValue());
                            setPathIntentId(sb);
                            setPathIntentType(data.getTextValue(), sb);
                        }
                    } else {
                        if (data.isTextual()) {
                            sb.append(data.getTextValue());
                        } else if (data.isDouble()) {
                            Double bandwidth = data.getDoubleValue();
                            sb.append(bandwidth);
                        } else if (data.isNumber()) {
                            Integer number = data.getIntValue();
                            sb.append(number);
                        }
                    }
                }
                System.out.println("constructed node " + sb.toString());
                sb.delete(0, sb.length());
                intentDesializer = new IntentDeserializer(graph, sb.toString().getBytes());
                Intent intent = intentDesializer.getIntent();
                intents.add(intent);
            }
        }

    }
    
    private void setPathIntentId(StringBuilder sb) {
        if (idBlock == null || nextIdBlock + 1 == idBlock.getSize()) {
            IControllerRegistryService controllerRegistry = getControllerRegistry();
            if (controllerRegistry != null) {
                idBlock = controllerRegistry.allocateUniqueIdBlock();
                nextIdBlock = idBlock.getStart();
                System.out.println("start block " + nextIdBlock + " end block " + idBlock.getEnd() + " size " + idBlock.getSize());
            }
        }
        if (idBlock != null) {
            sb.append(nextIdBlock);
            nextIdBlock++;
        }
    }
    
    private void setPathIntentType(final String pathIntentType, StringBuilder sb) {
        if (pathIntentType.equals("shortest-path")) {
            sb.append(ShortestPathIntent.class.getCanonicalName());
        } else if (pathIntentType.equals("constrainted-shortest-path")) {
            sb.append(ConstrainedShortestPathIntent.class.getCanonicalName());
        }
    }
    
    private IControllerRegistryService getControllerRegistry() {
        return (IControllerRegistryService) getContext().getAttributes().get(IControllerRegistryService.class.getCanonicalName());
    }
}
