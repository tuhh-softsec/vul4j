package net.floodlightcontroller.util.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.DeserializationContext;

import net.floodlightcontroller.util.IPv4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deserialize an IPv4 from a string.
 */
public class IPv4Deserializer extends JsonDeserializer<IPv4> {

    protected static Logger log = LoggerFactory.getLogger(IPv4Deserializer.class);

    @Override
    public IPv4 deserialize(JsonParser jp,
			    DeserializationContext ctxt)
	throws IOException, JsonProcessingException {

	IPv4 ipv4 = null;

	jp.nextToken();		// Move to JsonToken.START_OBJECT
	while (jp.nextToken() != JsonToken.END_OBJECT) {
	    String fieldname = jp.getCurrentName();
	    if ("value".equals(fieldname)) {
		String value = jp.getText();
		log.debug("Fieldname: " + fieldname + " Value: " + value);
		ipv4 = new IPv4(value);
	    }
	}
	return ipv4;
    }
}
