package net.floodlightcontroller.util.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.DeserializationContext;

import net.floodlightcontroller.util.FlowId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deserialize a Flow ID from a string.
 */
public class FlowIdDeserializer extends JsonDeserializer<FlowId> {

    protected static Logger log = LoggerFactory.getLogger(FlowIdDeserializer.class);

    @Override
    public FlowId deserialize(JsonParser jp,
			      DeserializationContext ctxt)
	throws IOException, JsonProcessingException {

	FlowId flowId = null;

	jp.nextToken();		// Move to JsonToken.START_OBJECT
	while (jp.nextToken() != JsonToken.END_OBJECT) {
	    String fieldname = jp.getCurrentName();
	    if ("value".equals(fieldname)) {
		String value = jp.getText();
		log.debug("Fieldname: " + fieldname + " Value: " + value);
		flowId = new FlowId(value);
	    }
	}
	return flowId;
    }
}
