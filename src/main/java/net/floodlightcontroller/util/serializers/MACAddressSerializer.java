package net.floodlightcontroller.util.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import net.floodlightcontroller.util.MACAddress;

/**
 * Serialize a MAC address as a string.
 */
public class MACAddressSerializer extends JsonSerializer<MACAddress> {

    @Override
    public void serialize(MACAddress mac, JsonGenerator jGen,
			  SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeStartObject();
	jGen.writeStringField("value", mac.toString());
	jGen.writeEndObject();
    }
}
