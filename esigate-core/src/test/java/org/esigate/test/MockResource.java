package org.esigate.test;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.esigate.output.Output;
import org.esigate.resource.Resource;

public class MockResource extends Resource {
	
	private int statusCode = 200;
	private String statusMessage = "OK";
	private Map<String, String> headers = new HashMap<String, String>();
	
	@Override
	public void render(Output output) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getStatusCode() {
		return statusCode;
	}

	@Override
	public String getStatusMessage() {
		return statusMessage;
	}

	@Override
	public String getHeader(String name) {
		return headers.get(name);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		String value = getHeader(name);
		if(value != null)
			return Collections.singleton(value);
		else 
			return Collections.emptySet();
	}

	@Override
	public Collection<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setHeader(String name, String value){
		headers.put(name, value);
	}
	
	public void setStatusCode(int statusCode){
		this.statusCode = statusCode;
	}
	
	public void setStatusMessage(String statusMessage){
		this.statusMessage = statusMessage;
	}
}
