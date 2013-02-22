package net.floodlightcontroller.util.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import net.floodlightcontroller.util.FlowId;

/**
 * Serialize a Flow ID as a hexadecimal string.
 */
public class FlowIdSerializer extends JsonSerializer<FlowId> {

    @Override
    public void serialize(FlowId flowId, JsonGenerator jGen,
			  SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeString(flowId.toString());
    }
}
