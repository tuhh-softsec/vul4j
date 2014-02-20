package net.onrc.onos.ofcontroller.networkgraph.serializers;

import java.io.IOException;

import net.onrc.onos.ofcontroller.networkgraph.Link;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.openflow.util.HexString;

public class LinkSerializer extends SerializerBase<Link> {

	public LinkSerializer() {
		super(Link.class);
	}

	@Override
	public void serialize(Link link, JsonGenerator jsonGenerator, 
			SerializerProvider serializerProvider)
			throws IOException, JsonGenerationException {
		jsonGenerator.writeStartObject();
		jsonGenerator.writeStringField("src-switch", 
				HexString.toHexString(link.getSrcSwitch().getDpid()));
		jsonGenerator.writeNumberField("src-port", 
				link.getSrcPort().getNumber());
		jsonGenerator.writeStringField("dst-switch", 
				HexString.toHexString(link.getDstSwitch().getDpid()));
		jsonGenerator.writeNumberField("dst-port", 
				link.getDstPort().getNumber());
		jsonGenerator.writeEndObject();
	}

}
