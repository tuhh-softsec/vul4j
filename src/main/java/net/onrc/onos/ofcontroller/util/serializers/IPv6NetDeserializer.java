package net.onrc.onos.ofcontroller.util.serializers;

import java.io.IOException;

import net.onrc.onos.ofcontroller.util.IPv6Net;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deserialize an IPv6Net address from a string.
 */
public class IPv6NetDeserializer extends JsonDeserializer<IPv6Net> {

    protected static Logger log = LoggerFactory.getLogger(IPv6NetDeserializer.class);

    @Override
    public IPv6Net deserialize(JsonParser jp,
			       DeserializationContext ctxt)
	throws IOException, JsonProcessingException {

	IPv6Net ipv6Net = null;

	jp.nextToken();		// Move to JsonToken.START_OBJECT
	while (jp.nextToken() != JsonToken.END_OBJECT) {
	    String fieldname = jp.getCurrentName();
	    if ("value".equals(fieldname)) {
		String value = jp.getText();
		log.debug("Fieldname: " + fieldname + " Value: " + value);
		ipv6Net = new IPv6Net(value);
	    }
	}
	return ipv6Net;
    }
}
