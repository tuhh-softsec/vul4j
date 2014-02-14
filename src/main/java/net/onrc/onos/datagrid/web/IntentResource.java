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
import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.ofcontroller.networkgraph.Port;
import net.onrc.onos.ofcontroller.networkgraph.Switch;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * 
 * @author nickkaranatsios
 */
public class IntentResource extends ServerResource {

    private final static org.slf4j.Logger log = LoggerFactory
	    .getLogger(IntentResource.class);
    private final String sep = ":";
    private IdBlock idBlock = null;
    private long nextIdBlock = 0;

    @Post("json")
    public void store(String jsonFlowIntent) {
	IDatagridService datagridService = (IDatagridService) getContext()
		.getAttributes().get(IDatagridService.class.getCanonicalName());
	if (datagridService == null) {
	    log.debug("FlowIntentResource ONOS Datagrid Service not found");
	    return;
	}
	INetworkGraphService networkGraphService = (INetworkGraphService) getContext()
		.getAttributes().get(
			INetworkGraphService.class.getCanonicalName());
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

	if (jNode != null) {
	    Kryo kryo = new Kryo();
	    parseJsonNode(kryo, jNode.getElements(), datagridService);
	    // datagridService.registerIntent(intents);
	}
    }

    private void parseJsonNode(Kryo kryo, Iterator<JsonNode> nodes,
	    IDatagridService datagridService) {
	StringBuilder sb = new StringBuilder();
	sb.ensureCapacity(256);

	while (nodes.hasNext()) {
	    JsonNode node = nodes.next();
	    if (node.isObject()) {
		JsonNode data = null;
		Iterator<String> fieldNames = node.getFieldNames();
		String intentId = null;
		String pathTypeName = null;
		long srcSwitch = 0, dstSwitch = 0;
		String srcMac = null, dstMac = null;
		long srcPort = 0, dstPort = 0;
		Double bandwidth = null;
		while (fieldNames.hasNext()) {
		    String fieldName = fieldNames.next();
		    data = node.get(fieldName);
		    if (fieldName.equals("type")) {
			if (data != null) {
			    System.out.println("type is not null "
				    + data.getTextValue());
			    // uuid = setPathIntentId();
			    pathTypeName = data.getTextValue();
			    setPathIntentType(pathTypeName, sb);
			}
		    } else if (fieldName.equals("intentId")) {
			intentId = data.getTextValue();
		    } else if (fieldName.equals("srcSwitch")) {
			srcSwitch = Long.decode(data.getTextValue());
		    } else if (fieldName.equals("dstSwitch")) {
			dstSwitch = Long.decode(data.getTextValue());
		    } else if (fieldName.equals("srcMac")) {
			srcMac = data.getTextValue();
		    } else if (fieldName.equals("dstMac")) {
			dstMac = data.getTextValue();
		    } else if (fieldName.equals("srcPort")) {
			srcPort = data.getLongValue();
		    } else if (fieldName.equals("dstPort")) {
			dstPort = data.getLongValue();
		    } else if (fieldName.equals("bandwidth")) {
			bandwidth = data.getDoubleValue();
		    }
		}
		if (pathTypeName.equals("shortest-path")) {
		    ShortestPathIntent spi = new ShortestPathIntent(intentId, 
                            srcSwitch, 
                            srcPort, 
                            MACAddress.valueOf(srcMac).toLong(), 
                            dstSwitch, 
                            dstPort,
			    MACAddress.valueOf(dstMac).toLong());
		    sb.append(toBytes(kryo, spi));

		} else {
		    ConstrainedShortestPathIntent cspi = new ConstrainedShortestPathIntent(intentId, 
                            srcSwitch, 
                            srcPort, 
                            MACAddress.valueOf(srcMac).toLong(), dstSwitch,
			    dstPort, MACAddress.valueOf(dstMac).toLong(),
			    bandwidth);
		    sb.append(toBytes(kryo, cspi));

		}
		System.out.println("constructed node " + sb.toString());
		// datagridService.registerIntent(Long.toString(uuid),
		// sb.toString().getBytes());
		sb.delete(0, sb.length());
	    }
	}
    }

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

    private void setPathIntentType(final String pathIntentType, StringBuilder sb) {
	String canonicalName = null;
	if (pathIntentType.equals("shortest-path")) {
	    canonicalName = ShortestPathIntent.class.getCanonicalName();
	    sb.append(ShortestPathIntent.class.getCanonicalName());
	} else if (pathIntentType.equals("constrained-shortest-path")) {
	    canonicalName = ShortestPathIntent.class.getCanonicalName();
	}
	sb.append(canonicalName);
	sb.append(sep);
    }

    private IControllerRegistryService getControllerRegistry() {
	return (IControllerRegistryService) getContext().getAttributes().get(
		IControllerRegistryService.class.getCanonicalName());
    }

    private byte[] toBytes(Kryo kryo, Object value) {
	Output output = new Output(1024);
        kryo.writeObject(output, value);
        output.close();
        return output.toBytes();
    }
}
