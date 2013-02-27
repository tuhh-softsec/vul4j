package net.floodlightcontroller.util.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import net.floodlightcontroller.util.Dpid;

/**
 * Serialize a DPID as a string.
 */
public class DpidSerializer extends JsonSerializer<Dpid> {

    @Override
    public void serialize(Dpid dpid, JsonGenerator jGen,
			  SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeStartObject();
	jGen.writeStringField("value", dpid.toString());
	jGen.writeEndObject();
    }
}
