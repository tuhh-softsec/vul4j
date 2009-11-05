package net.webassembletool;

import java.util.HashMap;
import java.util.Properties;

import net.webassembletool.output.StringOutput;

public class MockDriver extends Driver {
	private final HashMap<String, StringOutput> resources = new HashMap<String, StringOutput>();

	public MockDriver(String name) {
		super(name, new Properties());
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
