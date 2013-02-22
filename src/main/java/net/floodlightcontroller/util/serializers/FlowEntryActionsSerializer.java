package net.floodlightcontroller.util.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import net.floodlightcontroller.util.FlowEntryActions;

/**
 * Serialize a FlowEntryActions as a string.
 */
public class FlowEntryActionsSerializer extends JsonSerializer<FlowEntryActions> {

    @Override
    public void serialize(FlowEntryActions flowEntryActions,
			  JsonGenerator jGen,
			  SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeObject(flowEntryActions);
    }
}
