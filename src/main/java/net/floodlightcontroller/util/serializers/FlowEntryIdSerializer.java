package net.floodlightcontroller.util.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import net.floodlightcontroller.util.FlowEntryId;

/**
 * Serialize a Flow Entry ID as a hexadecimal string.
 */
public class FlowEntryIdSerializer extends JsonSerializer<FlowEntryId> {

    @Override
    public void serialize(FlowEntryId flowEntryId, JsonGenerator jGen,
			  SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeString(flowEntryId.toString());
    }
}
