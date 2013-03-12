package net.floodlightcontroller.util.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.DeserializationContext;

import net.floodlightcontroller.util.MACAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deserialize a MAC address from a string.
 */
public class MACAddressDeserializer extends JsonDeserializer<MACAddress> {

    protected static Logger log = LoggerFactory.getLogger(MACAddressDeserializer.class);

    @Override
    public MACAddress deserialize(JsonParser jp,
				  DeserializationContext ctxt)
	throws IOException, JsonProcessingException {

	MACAddress mac = null;

	jp.nextToken();		// Move to JsonToken.START_OBJECT
	while (jp.nextToken() != JsonToken.END_OBJECT) {
	    String fieldname = jp.getCurrentName();
	    if ("value".equals(fieldname)) {
		String value = jp.getText();
		log.debug("Fieldname: " + fieldname + " Value: " + value);
		mac = MACAddress.valueOf(value);
	    }
	}
	return mac;
    }
}
