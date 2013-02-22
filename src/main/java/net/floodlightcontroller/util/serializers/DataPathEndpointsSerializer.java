package net.floodlightcontroller.util.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import net.floodlightcontroller.util.DataPathEndpoints;

/**
 * Serialize a DataPathEndpoints as a string.
 */
public class DataPathEndpointsSerializer extends JsonSerializer<DataPathEndpoints> {

    @Override
    public void serialize(DataPathEndpoints dataPathEndpoints,
			  JsonGenerator jGen, SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeStartObject();
	jGen.writeObjectField("srcPort", dataPathEndpoints.srcPort());
	jGen.writeObjectField("dstPort", dataPathEndpoints.dstPort());
	jGen.writeEndObject();
    }
}
