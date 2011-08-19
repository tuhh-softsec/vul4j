package org.esigate;

import java.util.HashMap;
import java.util.Properties;

import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.ResourceContext;
import org.esigate.output.StringOutput;


public class MockDriver extends Driver {
	private final HashMap<String, StringOutput> resources = new HashMap<String, StringOutput>();

	public MockDriver(String name) {
		this(name, new Properties());
	}
	
	public MockDriver(String name, Properties props) {
		super(name, props);
		DriverFactory.put(name, this);
	}

	public void addResource(String relUrl, String content) {
		StringOutput stringOutput = new StringOutput();
		stringOutput.setStatusCode(200);
		stringOutput.setStatusMessage("OK");
		stringOutput.setCharsetName("ISO-8859-1");
		stringOutput.open();
		stringOutput.write(content);
		resources.put(relUrl, stringOutput);
	}

	@Override
	protected StringOutput getResourceAsString(ResourceContext target)
			throws HttpErrorPage {
		StringOutput result = resources.get(target.getRelUrl());
		if (result == null)
			throw new HttpErrorPage(404, "Not found", "The page: "
					+ target.getRelUrl() + " does not exist");
		return result;
	}

}
