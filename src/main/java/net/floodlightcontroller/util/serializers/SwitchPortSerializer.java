package net.floodlightcontroller.util.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import net.floodlightcontroller.util.SwitchPort;

/**
 * Serialize a SwitchPort as a string.
 */
public class SwitchPortSerializer extends JsonSerializer<SwitchPort> {

    @Override
    public void serialize(SwitchPort switchPort,
			  JsonGenerator jGen, SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeStartObject();
	jGen.writeObjectField("dpid", switchPort.dpid());
	jGen.writeObjectField("port", switchPort.port());
	jGen.writeEndObject();
    }
}
