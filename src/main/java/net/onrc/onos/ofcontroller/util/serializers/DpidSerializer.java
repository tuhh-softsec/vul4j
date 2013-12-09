package net.onrc.onos.ofcontroller.util.serializers;

import java.io.IOException;

import net.onrc.onos.ofcontroller.util.Dpid;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

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
