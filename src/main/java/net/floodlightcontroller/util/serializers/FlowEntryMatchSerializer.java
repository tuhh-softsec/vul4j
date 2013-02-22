package net.floodlightcontroller.util.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import net.floodlightcontroller.util.FlowEntryMatch;

/**
 * Serialize a FlowEntryMatch as a string.
 */
public class FlowEntryMatchSerializer extends JsonSerializer<FlowEntryMatch> {

    @Override
    public void serialize(FlowEntryMatch flowEntryMatch,
			  JsonGenerator jGen, SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeStartObject();
	jGen.writeObjectField("srcMac", flowEntryMatch.srcMac());
	jGen.writeObjectField("dstMac", flowEntryMatch.dstMac());
	jGen.writeObjectField("srcIPv4Net", flowEntryMatch.srcIPv4Net());
	jGen.writeObjectField("dstIPv4Net", flowEntryMatch.dstIPv4Net());
	jGen.writeEndObject();
    }
}
