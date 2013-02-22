package net.floodlightcontroller.util.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import net.floodlightcontroller.util.CallerId;

/**
 * Serialize a Caller ID as a string.
 */
public class CallerIdSerializer extends JsonSerializer<CallerId> {

    @Override
    public void serialize(CallerId callerId, JsonGenerator jGen,
			  SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeString(callerId.toString());
    }
}
