package net.onrc.onos.ofcontroller.util.serializers;

import java.io.IOException;

import net.onrc.onos.ofcontroller.util.FlowId;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

/**
 * Serialize a Flow ID as a hexadecimal string.
 */
public class FlowIdSerializer extends JsonSerializer<FlowId> {

    @Override
    public void serialize(FlowId flowId, JsonGenerator jGen,
			  SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeStartObject();
	jGen.writeStringField("value", flowId.toString());
	jGen.writeEndObject();
    }
}
