package net.floodlightcontroller.util.serializers;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import net.floodlightcontroller.util.DataPath;
import net.floodlightcontroller.util.FlowEntry;

/**
 * Serialize a DataPath as a string.
 */
public class DataPathSerializer extends JsonSerializer<DataPath> {

    @Override
    public void serialize(DataPath dataPath,
			  JsonGenerator jGen, SerializerProvider serializer)
	throws IOException, JsonProcessingException {
	jGen.writeStartObject();
	jGen.writeObjectField("srcPort", dataPath.srcPort());
	jGen.writeArrayFieldStart("flowEntries");
	for (FlowEntry fe: dataPath.flowEntries()) {
	    jGen.writeObject(fe);
	}
	jGen.writeEndArray();
	jGen.writeObjectField("dstPort", dataPath.dstPort());
	jGen.writeEndObject();
    }
}
