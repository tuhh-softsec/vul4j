package net.onrc.onos.ofcontroller.util.serializers;

import java.io.IOException;

import net.onrc.onos.ofcontroller.util.IPv4Net;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

/**
 * Serialize an IPv4Net address as a string.
 */
public class IPv4NetSerializer extends JsonSerializer<IPv4Net> {

    @Override
    public void serialize(IPv4Net ipv4Net, JsonGenerator jGen,
			  SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeStartObject();
	jGen.writeStringField("value", ipv4Net.toString());
	jGen.writeEndObject();
    }
}
