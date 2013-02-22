package net.floodlightcontroller.util.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import net.floodlightcontroller.util.IPv6;

/**
 * Serialize an IPv6 address as a string.
 */
public class IPv6Serializer extends JsonSerializer<IPv6> {

    @Override
    public void serialize(IPv6 ipv6, JsonGenerator jGen,
			  SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeString(ipv6.toString());
    }
}
