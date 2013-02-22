package net.floodlightcontroller.util.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import net.floodlightcontroller.util.FlowPath;

/**
 * Serialize a FlowPath as a string.
 */
public class FlowPathSerializer extends JsonSerializer<FlowPath> {

    @Override
    public void serialize(FlowPath flowPath,
			  JsonGenerator jGen, SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeStartObject();
	jGen.writeObjectField("flowId", flowPath.flowId());
	jGen.writeObjectField("installerId", flowPath.installerId());
	jGen.writeObjectField("dataPath", flowPath.dataPath());
	jGen.writeEndObject();
    }
}
