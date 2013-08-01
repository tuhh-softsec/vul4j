package net.onrc.onos.ofcontroller.flowmanager.web;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatapathSummarySerializer extends JsonSerializer<String>{
	static Logger log = LoggerFactory.getLogger(DatapathSummarySerializer.class);
	
	@Override
	public void serialize(String datapathSummary, JsonGenerator jGen,
			SerializerProvider serializer) throws IOException,
			JsonProcessingException {
		
		String[] flowEntries = datapathSummary.split(";");
		if (flowEntries.length < 2){
			log.debug("datapathSummary string to short to parse: {}", 
					datapathSummary);
			jGen.writeStartObject();
			jGen.writeEndObject();
			return;
		}
		
		String[] srcFlowEntry = flowEntries[0].split("/");
		String[] dstFlowEntry = flowEntries[flowEntries.length - 1].split("/");
		if (srcFlowEntry.length != 3 || dstFlowEntry.length != 3){
			log.debug("Malformed datapathSummary string: {}", datapathSummary);
			jGen.writeStartObject();
			jGen.writeEndObject();
			return;
		}
		
		jGen.writeStartObject();
		
		/*
		jGen.writeObjectFieldStart("srcPort");
		jGen.writeObjectFieldStart("dpid");
		jGen.writeStringField("value", srcFlowEntry[1]);
		jGen.writeEndObject();
		jGen.writeObjectFieldStart("port");
		jGen.writeStringField("value", srcFlowEntry[0]);
		jGen.writeEndObject();
		jGen.writeEndObject();
		
		jGen.writeObjectFieldStart("dstPort");
		jGen.writeObjectFieldStart("dpid");
		jGen.writeStringField("value", srcFlowEntry[1]);
		jGen.writeEndObject();
		jGen.writeObjectFieldStart("port");
		jGen.writeStringField("value", srcFlowEntry[2]);
		jGen.writeEndObject();
		jGen.writeEndObject();
		*/
		jGen.writeArrayFieldStart("flowEntries");
		
		for (String flowEntryString : flowEntries){
			String[] flowEntry = flowEntryString.split("/");
			if (flowEntry.length != 3){
				log.debug("Malformed datapathSummary string: {}", datapathSummary);
				jGen.writeStartObject();
				jGen.writeEndObject();
				continue;
			}
			
			jGen.writeStartObject();
			jGen.writeObjectFieldStart("dpid");
			jGen.writeStringField("value", flowEntry[1]);
			jGen.writeEndObject();
			jGen.writeEndObject();
		}
		
		jGen.writeEndArray();
		
		jGen.writeEndObject();
	}

}
