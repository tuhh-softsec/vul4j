package net.onrc.onos.ofcontroller.util.serializers;

import java.io.IOException;

import net.onrc.onos.ofcontroller.util.IPv6Net;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

/**
 * Serialize an IPv6Net address as a string.
 */
public class IPv6NetSerializer extends JsonSerializer<IPv6Net> {

    @Override
    public void serialize(IPv6Net ipv6Net, JsonGenerator jGen,
			  SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeStartObject();
	jGen.writeStringField("value", ipv6Net.toString());
	jGen.writeEndObject();
    }
}
