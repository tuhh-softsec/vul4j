package net.floodlightcontroller.util.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import net.floodlightcontroller.util.FlowEntry;

/**
 * Serialize a FlowEntry as a string.
 */
public class FlowEntrySerializer extends JsonSerializer<FlowEntry> {

    @Override
    public void serialize(FlowEntry flowEntry,
			  JsonGenerator jGen, SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeStartObject();
	jGen.writeObjectField("flowEntryId", flowEntry.flowEntryId());
	jGen.writeObjectField("flowEntryMatch", flowEntry.flowEntryMatch());
	jGen.writeObjectField("flowEntryActions",
			      flowEntry.flowEntryActions());
	jGen.writeObjectField("dpid", flowEntry.dpid());
	jGen.writeObjectField("inPort", flowEntry.inPort());
	jGen.writeObjectField("outPort", flowEntry.outPort());
	jGen.writeObjectField("flowEntryUserState",
			      flowEntry.flowEntryUserState());
	jGen.writeObjectField("flowEntrySwitchState",
			      flowEntry.flowEntrySwitchState());
	jGen.writeObjectField("flowEntryErrorState",
			      flowEntry.flowEntryErrorState());
	jGen.writeEndObject();
    }
}
