package net.floodlightcontroller.util.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import net.floodlightcontroller.util.FlowEntryErrorState;

/**
 * Serialize a Flow Entry Error State as a string.
 */
public class FlowEntryErrorStateSerializer extends JsonSerializer<FlowEntryErrorState> {

    @Override
    public void serialize(FlowEntryErrorState flowEntryErrorState,
			  JsonGenerator jGen, SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeStartObject();
	jGen.writeNumberField("type", flowEntryErrorState.type());
	jGen.writeNumberField("code", flowEntryErrorState.code());
	jGen.writeEndObject();
    }
}
