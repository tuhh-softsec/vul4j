package net.onrc.onos.ofcontroller.util.serializers;

import java.io.IOException;

import net.onrc.onos.ofcontroller.util.IPv4;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

/**
 * Serialize an IPv4 address as a string.
 */
public class IPv4Serializer extends JsonSerializer<IPv4> {

    @Override
    public void serialize(IPv4 ipv4, JsonGenerator jGen,
			  SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeStartObject();
	jGen.writeStringField("value", ipv4.toString());
	jGen.writeEndObject();
    }
}
