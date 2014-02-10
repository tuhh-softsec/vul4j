package net.onrc.onos.ofcontroller.networkgraph.serializers;

import java.io.IOException;

import net.onrc.onos.ofcontroller.networkgraph.Port;
import net.onrc.onos.ofcontroller.networkgraph.Switch;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.openflow.util.HexString;

public class SwitchSerializer extends SerializerBase<Switch> {

	public SwitchSerializer() {
		super(Switch.class);
	}

	@Override
	public void serialize(Switch sw, JsonGenerator jsonGenerator,
			SerializerProvider serializerProvider) throws IOException,
			JsonProcessingException {
		
		jsonGenerator.writeStartObject();
		jsonGenerator.writeStringField("dpid", HexString.toHexString(sw.getDpid()));
		jsonGenerator.writeStringField("state", "ACTIVE");
		jsonGenerator.writeArrayFieldStart("ports");
		for (Port port : sw.getPorts()) {
			jsonGenerator.writeObject(port);
		}
		jsonGenerator.writeEndArray();
		jsonGenerator.writeEndObject();
	}

}
